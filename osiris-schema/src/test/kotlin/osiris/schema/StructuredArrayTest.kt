package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import io.kotest.matchers.shouldBe
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.typeOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StructuredArrayTest {
  @Test
  fun nullable(): Unit =
    runTest {
      Structured.schema<List<String>?>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(
                  JsonArraySchema.builder().apply {
                    items(JsonStringSchema.builder().build())
                  }.build(),
                  JsonNullSchema,
                )
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `nullable elements`(): Unit =
    runTest {
      Structured.schema<List<String?>>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonArraySchema.builder().apply {
                items(
                  JsonAnyOfSchema.builder().apply {
                    anyOf(JsonStringSchema.builder().build(), JsonNullSchema)
                  }.build(),
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
        type = List::class.createType(
          arguments = listOf(KTypeProjection.invariant(typeOf<String>())),
          annotations = listOf(Structured.Description("An array")),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(
            JsonArraySchema.builder().apply {
              description("An array")
              items(JsonStringSchema.builder().build())
            }.build(),
          )
        }.build(),
      )
    }

  @Test
  fun list(): Unit =
    runTest {
      Structured.schema<List<String>>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonArraySchema.builder().apply {
                items(JsonStringSchema.builder().build())
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun set(): Unit =
    runTest {
      Structured.schema<Set<String>>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonArraySchema.builder().apply {
                items(JsonStringSchema.builder().build())
              }.build(),
            )
          }.build(),
        )
    }
}
