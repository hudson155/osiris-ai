package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorClassTest {
  @Serializable
  @Schema.Description("nested schema")
  data class NestedSchema(val value: String)

  @Serializable
  @Schema.Description("wrapper schema")
  data class WrapperSchema(val nested: NestedSchema)

  @Serializable
  @Schema.Description("wrapper schema")
  data class WrapperSchemaWithDescription(@Schema.Description("my nested") val nested: NestedSchema)

  @Serializable
  data class ComplexSchema(
    val boolean: Boolean,
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val string: String,
    val nested: NestedSchema,
  ) {
    @Serializable
    data class NestedSchema(
      val boolean: Boolean,
      val int: Int,
      val long: Long,
      val float: Float,
      val double: Double,
      val string: String,
    )
  }

  @Test
  fun `class, with description on class`(): Unit =
    runTest {
      SchemaGenerator.generate<WrapperSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("wrapper schema")
            addProperty(
              "nested",
              JsonObjectSchema.builder().apply {
                description("nested schema")
                addStringProperty("value")
                required("value")
              }.build(),
            )
            required("nested")
          }.build(),
        )
    }

  @Test
  fun `class, with description on property`(): Unit =
    runTest {
      SchemaGenerator.generate<WrapperSchemaWithDescription>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("wrapper schema")
            addProperty(
              "nested",
              JsonObjectSchema.builder().apply {
                description("my nested")
                addStringProperty("value")
                required("value")
              }.build(),
            )
            required("nested")
          }.build(),
        )
    }

  @Test
  fun `class, complex`(): Unit =
    runTest {
      SchemaGenerator.generate<ComplexSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addBooleanProperty("boolean")
            addIntegerProperty("int")
            addIntegerProperty("long")
            addNumberProperty("float")
            addNumberProperty("double")
            addStringProperty("string")
            addProperty(
              "nested",
              JsonObjectSchema.builder().apply {
                addBooleanProperty("boolean")
                addIntegerProperty("int")
                addIntegerProperty("long")
                addNumberProperty("float")
                addNumberProperty("double")
                addStringProperty("string")
                required("boolean", "int", "long", "float", "double", "string")
              }.build(),
            )
            required("boolean", "int", "long", "float", "double", "string", "nested")
          }.build(),
        )
    }
}
