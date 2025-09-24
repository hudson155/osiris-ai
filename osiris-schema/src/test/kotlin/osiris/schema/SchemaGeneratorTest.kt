package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorTest {
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
  fun `object, with description on class`(): Unit =
    runTest {
      @Serializable
      @Schema.Description("my nested")
      data class NestedSchema(val value: String)

      @Serializable
      @Schema.Description("my object")
      data class TestSchema(val nested: NestedSchema)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("my object")
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
  fun `object, with description on property`(): Unit =
    runTest {
      @Serializable
      @Schema.Description("ignored")
      data class NestedSchema(val value: String)

      @Serializable
      @Schema.Description("my object")
      data class TestSchema(@Schema.Description("my nested") val nested: NestedSchema)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("my object")
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
  fun `object, complex`(): Unit =
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

  @Test
  fun boolean(): Unit =
    runTest {
      SchemaGenerator.generate<Boolean>()
        .shouldBe(JsonBooleanSchema.builder().build())
    }

  @Test
  fun `boolean, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: Boolean)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addBooleanProperty("value")
            required("value")
          }.build(),
        )
    }

  @Test
  fun `boolean, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my boolean") val value: Boolean)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addBooleanProperty("value", "my boolean")
            required("value")
          }.build(),
        )
    }

  @Test
  fun int(): Unit =
    runTest {
      SchemaGenerator.generate<Int>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `int, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: Int)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addIntegerProperty("value")
            required("value")
          }.build(),
        )
    }

  @Test
  fun `int, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my int") val value: Int)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addIntegerProperty("value", "my int")
            required("value")
          }.build(),
        )
    }

  @Test
  fun long(): Unit =
    runTest {
      SchemaGenerator.generate<Long>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `long, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: Long)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addIntegerProperty("value")
            required("value")
          }.build(),
        )
    }

  @Test
  fun `long, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my long") val value: Long)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addIntegerProperty("value", "my long")
            required("value")
          }.build(),
        )
    }

  @Test
  fun float(): Unit =
    runTest {
      SchemaGenerator.generate<Float>()
        .shouldBe(JsonNumberSchema.builder().build())
    }

  @Test
  fun `float, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: Float)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addNumberProperty("value")
            required("value")
          }.build(),
        )
    }

  @Test
  fun `float, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my float") val value: Float)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addNumberProperty("value", "my float")
            required("value")
          }.build(),
        )
    }

  @Test
  fun double(): Unit =
    runTest {
      SchemaGenerator.generate<Double>()
        .shouldBe(JsonNumberSchema.builder().build())
    }

  @Test
  fun `double, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: Double)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addNumberProperty("value")
            required("value")
          }.build(),
        )
    }

  @Test
  fun `double, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my double") val value: Double)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addNumberProperty("value", "my double")
            required("value")
          }.build(),
        )
    }

  @Test
  fun string(): Unit =
    runTest {
      SchemaGenerator.generate<String>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `string, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: String)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addStringProperty("value")
            required("value")
          }.build(),
        )
    }

  @Test
  fun `string, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my string") val value: String)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addStringProperty("value", "my string")
            required("value")
          }.build(),
        )
    }
}
