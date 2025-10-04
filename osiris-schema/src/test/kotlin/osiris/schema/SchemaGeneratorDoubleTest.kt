package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorDoubleTest {
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
}
