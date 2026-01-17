package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import io.kotest.matchers.shouldBe
import kotlin.reflect.full.createType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Includes all string-like types that kairo-serialization supports,
 * except [ProtectedString].
 */
internal class StructuredStringTest {
  @Test
  fun nullable(): Unit =
    runTest {
      Structured.schema<String?>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(JsonStringSchema.builder().build(), JsonNullSchema)
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.schema(
        type = String::class.createType(
          annotations = listOf(Structured.Description("A string")),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(
            JsonStringSchema.builder().apply {
              description("A string")
            }.build(),
          )
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to String)`(): Unit =
    runTest {
      Structured.schema(
        type = Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.String)),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(JsonStringSchema.builder().build())
        }.build(),
      )
    }

  @Test
  fun `well-known (CharArray)`(): Unit =
    runTest {
      Structured.schema<CharArray>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Char)`(): Unit =
    runTest {
      Structured.schema<Char>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (String)`(): Unit =
    runTest {
      Structured.schema<String>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Java UUID)`(): Unit =
    runTest {
      Structured.schema<java.util.UUID>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Kotlin Uuid)`(): Unit =
    runTest {
      Structured.schema<kotlin.uuid.Uuid>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build(),
        )
    }
}
