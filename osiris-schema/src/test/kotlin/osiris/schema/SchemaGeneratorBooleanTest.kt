package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorBooleanTest {
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
}
