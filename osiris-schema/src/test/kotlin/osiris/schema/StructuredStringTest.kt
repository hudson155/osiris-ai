package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
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
      Structured.generate<String?>()
        .shouldBe(
          JsonAnyOfSchema.builder().apply {
            anyOf(JsonStringSchema.builder().build(), JsonNullSchema)
          }.build()
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.generate(
        String::class.createType(
          annotations = listOf(Structured.Description("A string"))
        )
      ).shouldBe(
        JsonStringSchema.builder().apply {
          description("A string")
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to String)`(): Unit =
    runTest {
      Structured.generate(
        Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.String))
        )
      ).shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (CharArray)`(): Unit =
    runTest {
      Structured.generate<CharArray>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Char)`(): Unit =
    runTest {
      Structured.generate<Char>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (String)`(): Unit =
    runTest {
      Structured.generate<String>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java UUID)`(): Unit =
    runTest {
      Structured.generate<java.util.UUID>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin Uuid)`(): Unit =
    runTest {
      Structured.generate<kotlin.uuid.Uuid>()
        .shouldBe(JsonStringSchema.builder().build())
    }
}
