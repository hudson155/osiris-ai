package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorStringTest {
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
