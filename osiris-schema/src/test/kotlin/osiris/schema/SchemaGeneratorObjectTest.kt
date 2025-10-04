package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorObjectTest {
  @Serializable
  @Schema.Description("nested schema")
  data object NestedSchema

  @Serializable
  @Schema.Description("wrapper schema")
  data class WrapperSchema(val nested: NestedSchema)

  @Serializable
  @Schema.Description("wrapper schema")
  data class WrapperSchemaWithDescription(@Schema.Description("my nested") val nested: NestedSchema)

  @Test
  fun `object, with description on class`(): Unit =
    runTest {
      SchemaGenerator.generate<WrapperSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("wrapper schema")
            addProperty(
              "nested",
              JsonObjectSchema.builder().apply {
                description("nested schema")
                required()
              }.build(),
            )
            required("nested")
          }.build(),
        )
    }

  @Test
  fun `object, with description on property`(): Unit =
    runTest {
      SchemaGenerator.generate<WrapperSchemaWithDescription>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            description("wrapper schema")
            addProperty(
              "nested",
              JsonObjectSchema.builder().apply {
                description("my nested")
                required()
              }.build(),
            )
            required("nested")
          }.build(),
        )
    }
}
