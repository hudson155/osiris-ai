package osiris.schema

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.kotest.matchers.shouldBe
import kotlin.reflect.full.createType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StructuredObjectTest {
  @Structured.Name("My data object")
  internal data object DataObject

  @Structured.Name("My data class")
  internal data class DataClass(
    val boolean: Boolean,
    val ints: List<Int>,
    val nested: Nested,
  ) {
    @Structured.Description("My nested")
    internal data class Nested(
      val string: String,
    )
  }

  @Structured.Name("My animal")
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes(
    JsonSubTypes.Type(Animal.Dog::class, name = "Dog"),
    JsonSubTypes.Type(Animal.Cat::class, name = "Cat"),
  )
  internal sealed class Animal {
    abstract val name: String

    @Structured.Discriminator("Dog")
    @Structured.Description("My dog")
    internal data class Dog(override val name: String, val barksPerMinute: Int) : Animal()

    @Structured.Discriminator("Cat")
    @Structured.Description("My cat")
    internal data class Cat(override val name: String, val napsPerDay: Int) : Animal()
  }

  @Test
  fun nullable(): Unit =
    runTest {
      Structured.schema<DataObject?>()
        .shouldBe(
          JsonSchema.builder().apply {
            name("My data object")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(
                  JsonObjectSchema.builder().apply {
                    required()
                    additionalProperties(false)
                  }.build(),
                  JsonNullSchema,
                )
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.schema(
        DataObject::class.createType(
          annotations = listOf(Structured.Description("An object")),
        ),
      ).shouldBe(
        JsonSchema.builder().apply {
          name("My data object")
          rootElement(
            JsonObjectSchema.builder().apply {
              description("An object")
              required()
              additionalProperties(false)
            }.build(),
          )
        }.build(),
      )
    }

  @Test
  fun `data object`(): Unit =
    runTest {
      Structured.schema<DataObject>()
        .shouldBe(
          JsonSchema.builder().apply {
            name("My data object")
            rootElement(
              JsonObjectSchema.builder().apply {
                required()
                additionalProperties(false)
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `data class`(): Unit =
    runTest {
      Structured.schema<DataClass>()
        .shouldBe(
          JsonSchema.builder().apply {
            name("My data class")
            rootElement(
              JsonObjectSchema.builder().apply {
                addBooleanProperty("boolean")
                addProperty(
                  "ints",
                  JsonArraySchema.builder().apply {
                    items(JsonIntegerSchema.builder().build())
                  }.build(),
                )
                addProperty(
                  "nested",
                  JsonObjectSchema.builder().apply {
                    description("My nested")
                    addStringProperty("string")
                    required("string")
                    additionalProperties(false)
                  }.build(),
                )
                required("boolean", "ints", "nested")
                additionalProperties(false)
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `sealed class`(): Unit =
    runTest {
      Structured.schema<Animal>()
        .shouldBe(
          JsonSchema.builder().apply {
            name("My animal")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(
                  JsonObjectSchema.builder().apply {
                    description("My cat")
                    addEnumProperty("type", listOf("Cat"))
                    addStringProperty("name")
                    addIntegerProperty("napsPerDay")
                    required("type", "name", "napsPerDay")
                    additionalProperties(false)
                  }.build(),
                  JsonObjectSchema.builder().apply {
                    description("My dog")
                    addEnumProperty("type", listOf("Dog"))
                    addStringProperty("name")
                    addIntegerProperty("barksPerMinute")
                    required("type", "name", "barksPerMinute")
                    additionalProperties(false)
                  }.build(),
                )
              }.build(),
            )
          }.build(),
        )
    }
}
