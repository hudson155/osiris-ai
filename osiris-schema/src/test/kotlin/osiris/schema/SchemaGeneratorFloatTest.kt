package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorFloatTest {
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
}
