package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.kotest.matchers.shouldBe
import kotlin.reflect.full.createType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StructuredBooleanTest {
  @Test
  fun nullable(): Unit =
    runTest {
      Structured.schema<Boolean?>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(JsonBooleanSchema.builder().build(), JsonNullSchema)
              }.build()
            )
          }.build(),
        )
    }

  @Test
  fun `with description`(): Unit =
    runTest {
      Structured.schema(
        type = Boolean::class.createType(
          annotations = listOf(Structured.Description("A boolean")),
        ),
        name = "schema"
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(
            JsonBooleanSchema.builder().apply {
              description("A boolean")
            }.build()
          )
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to Boolean)`(): Unit =
    runTest {
      Structured.schema(
        type = Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.Boolean)),
        ),
        name = "schema"
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(JsonBooleanSchema.builder().build())
        }.build()
      )
    }

  @Test
  fun `well-known (Boolean)`(): Unit =
    runTest {
      Structured.schema<Boolean>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonBooleanSchema.builder().build())
          }.build()
        )
    }
}
