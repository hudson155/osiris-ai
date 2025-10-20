package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorSealedTest {
  @Serializable
  @Schema.Description("nested schema")
  sealed class NestedSchema {
    @Serializable
    @SerialName("first")
    @Schema.Description("nested schema first")
    data class First(val value: String) : NestedSchema()

    @Serializable
    @SerialName("second")
    @Schema.Description("nested schema second")
    data object Second : NestedSchema()
  }

  @Serializable
  @Schema.Description("wrapper schema")
  data class WrapperSchema(val nested: NestedSchema)

  @Serializable
  @Schema.Description("wrapper schema")
  data class WrapperSchemaWithDescription(@Schema.Description("my nested") val nested: NestedSchema)

  @Test
  fun `sealed, with description on class`(): Unit =
    runTest {
      SchemaGenerator.generate<WrapperSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("wrapper schema")
            addProperty(
              "nested",
              JsonAnyOfSchema.builder().apply {
                description("nested schema")
                anyOf(
                  JsonObjectSchema.builder().apply {
                    description("nested schema first")
                    addEnumProperty("type", listOf("first"))
                    addStringProperty("value")
                    required("type", "value")
                  }.build(),
                  JsonObjectSchema.builder().apply {
                    description("nested schema second")
                    addEnumProperty("type", listOf("second"))
                    required("type")
                  }.build(),
                )
              }.build(),
            )
            required("nested")
          }.build(),
        )
    }

  @Test
  fun `sealed, with description on property`(): Unit =
    runTest {
      SchemaGenerator.generate<WrapperSchemaWithDescription>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("wrapper schema")
            addProperty(
              "nested",
              JsonAnyOfSchema.builder().apply {
                description("my nested")
                anyOf(
                  JsonObjectSchema.builder().apply {
                    description("nested schema first")
                    addEnumProperty("type", listOf("first"))
                    addStringProperty("value")
                    required("type", "value")
                  }.build(),
                  JsonObjectSchema.builder().apply {
                    description("nested schema second")
                    addEnumProperty("type", listOf("second"))
                    required("type")
                  }.build(),
                )
              }.build(),
            )
            required("nested")
          }.build(),
        )
    }
}
