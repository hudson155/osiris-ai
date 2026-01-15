package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonEnumSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import io.kotest.matchers.shouldBe
import kotlin.reflect.full.createType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StructuredEnumTest {
  internal enum class Genre {
    Fantasy,
    History,
    Religion,
    Romance,
    Science,
    ScienceFiction,
  }

  @Test
  fun nullable(): Unit =
    runTest {
      Structured.generate<Genre?>()
        .shouldBe(
          JsonAnyOfSchema.builder().apply {
            anyOf(
              JsonEnumSchema.builder().apply {
                enumValues(
                  "Fantasy",
                  "History",
                  "Religion",
                  "Romance",
                  "Science",
                  "ScienceFiction",
                )
              }.build(),
              JsonNullSchema,
            )
          }.build(),
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.generate(
        Genre::class.createType(
          annotations = listOf(Structured.Description("An enum")),
        ),
      ).shouldBe(
        JsonEnumSchema.builder().apply {
          description("An enum")
          enumValues(
            "Fantasy",
            "History",
            "Religion",
            "Romance",
            "Science",
            "ScienceFiction",
          )
        }.build(),
      )
    }

  @Test
  fun test(): Unit =
    runTest {
      Structured.generate<Genre>()
        .shouldBe(
          JsonEnumSchema.builder().apply {
            enumValues(
              "Fantasy",
              "History",
              "Religion",
              "Romance",
              "Science",
              "ScienceFiction",
            )
          }.build(),
        )
    }
}
