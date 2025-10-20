package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonEnumSchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

// TODO: Add testing for nulls. I don't have it right now because JsonNullSchema doesn't implement equals().
// TODO: Write evals.

internal class SchemaGeneratorEnumTest {
  internal enum class Genre {
    Fantasy,
    History,
    Religion,
    Romance,
    Science,
    ScienceFiction,
  }

  @Test
  fun string(): Unit =
    runTest {
      SchemaGenerator.generate<Genre>()
        .shouldBe(
          JsonEnumSchema.builder().apply {
            enumValues("Fantasy", "History", "Religion", "Romance", "Science", "ScienceFiction")
          }.build(),
        )
    }

  @Test
  fun `string, in object`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(val value: Genre)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addEnumProperty(
              "value",
              listOf("Fantasy", "History", "Religion", "Romance", "Science", "ScienceFiction"),
            )
            required("value")
          }.build(),
        )
    }

  @Test
  fun `string, with description`(): Unit =
    runTest {
      @Serializable
      data class TestSchema(@Schema.Description("my enum") val value: Genre)

      SchemaGenerator.generate<TestSchema>()
        .shouldBe(
          JsonObjectSchema.builder().apply {
            addEnumProperty(
              "value",
              listOf("Fantasy", "History", "Religion", "Romance", "Science", "ScienceFiction"),
              "my enum",
            )
            required("value")
          }.build(),
        )
    }
}
