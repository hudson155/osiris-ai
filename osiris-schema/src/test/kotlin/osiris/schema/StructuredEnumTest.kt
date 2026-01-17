package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonEnumSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
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
      Structured.schema<Genre?>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
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
          }.build(),
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.schema(
        type = Genre::class.createType(
          annotations = listOf(Structured.Description("An enum")),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(
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
        }.build(),
      )
    }

  @Test
  fun test(): Unit =
    runTest {
      Structured.schema<Genre>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
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
          }.build(),
        )
    }
}
