package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import io.kotest.matchers.shouldBe
import kotlin.reflect.full.createType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StructuredBooleanTest {
  @Test
  fun nullable(): Unit =
    runTest {
      Structured.generate<Boolean?>()
        .shouldBe(
          JsonAnyOfSchema.builder().apply {
            anyOf(JsonBooleanSchema.builder().build(), JsonNullSchema)
          }.build()
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.generate(
        Boolean::class.createType(
          annotations = listOf(Structured.Description("A boolean"))
        )
      ).shouldBe(
        JsonBooleanSchema.builder().apply {
          description("A boolean")
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to Boolean)`(): Unit =
    runTest {
      Structured.generate(
        Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.Boolean))
        )
      ).shouldBe(JsonBooleanSchema.builder().build())
    }

  @Test
  fun `well-known (Boolean)`(): Unit =
    runTest {
      Structured.generate<Boolean>()
        .shouldBe(JsonBooleanSchema.builder().build())
    }
}
