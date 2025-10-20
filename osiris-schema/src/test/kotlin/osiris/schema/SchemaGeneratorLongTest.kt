package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorLongTest {
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
      data class TestSchema(
        val value: Long,
        val values: List<Long>,
      )

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addIntegerProperty("value")
            addProperty(
              "values",
              JsonArraySchema.builder().apply {
                items(JsonIntegerSchema.builder().build())
              }.build(),
            )
            required("value", "values")
          }.build(),
        )
    }

  @Test
  fun `long, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(
        @Schema.Description("my long") val value: Long,
        @Schema.Description("my longs") val values: List<Long>,
      )

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addIntegerProperty("value", "my long")
            addProperty(
              "values",
              JsonArraySchema.builder().apply {
                description("my longs")
                items(JsonIntegerSchema.builder().build())
              }.build(),
            )
            required("value", "values")
          }.build(),
        )
    }
}
