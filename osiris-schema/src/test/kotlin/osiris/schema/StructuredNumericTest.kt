package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
import dev.langchain4j.model.chat.request.json.JsonSchema
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.full.createType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Includes all numeric types that kairo-serialization supports.
 */
internal class StructuredNumericTest {
  @Test
  fun `nullable (Integer)`(): Unit =
    runTest {
      Structured.schema<Long?>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(JsonIntegerSchema.builder().build(), JsonNullSchema)
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `nullable (Number)`(): Unit =
    runTest {
      Structured.schema<Double?>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(
              JsonAnyOfSchema.builder().apply {
                anyOf(JsonNumberSchema.builder().build(), JsonNullSchema)
              }.build(),
            )
          }.build(),
        )
    }

  @Test
  fun `with description (Integer)`(): Unit =
    runTest {
      Structured.schema(
        type = Long::class.createType(
          annotations = listOf(Structured.Description("An integer")),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(
            JsonIntegerSchema.builder().apply {
              description("An integer")
            }.build(),
          )
        }.build(),
      )
    }

  @Test
  fun `with description (Number)`(): Unit =
    runTest {
      Structured.schema(
        type = Double::class.createType(
          annotations = listOf(Structured.Description("A number")),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(
            JsonNumberSchema.builder().apply {
              description("A number")
            }.build(),
          )
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to Integer)`(): Unit =
    runTest {
      Structured.schema(
        type = Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.Integer)),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(JsonIntegerSchema.builder().build())
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to Number)`(): Unit =
    runTest {
      Structured.schema(
        type = Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.Number)),
        ),
        name = "schema",
      ).shouldBe(
        JsonSchema.builder().apply {
          name("schema")
          rootElement(JsonNumberSchema.builder().build())
        }.build(),
      )
    }

  @Test
  fun `well-known (BigDecimal)`(): Unit =
    runTest {
      Structured.schema<BigDecimal>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonNumberSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (BigInteger)`(): Unit =
    runTest {
      Structured.schema<BigInteger>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Byte)`(): Unit =
    runTest {
      Structured.schema<Byte>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (UByte)`(): Unit =
    runTest {
      Structured.schema<UByte>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Double)`(): Unit =
    runTest {
      Structured.schema<Double>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonNumberSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Float)`(): Unit =
    runTest {
      Structured.schema<Float>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonNumberSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Int)`(): Unit =
    runTest {
      Structured.schema<Int>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (UInt)`(): Unit =
    runTest {
      Structured.schema<UInt>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Long)`(): Unit =
    runTest {
      Structured.schema<Long>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (ULong)`(): Unit =
    runTest {
      Structured.schema<ULong>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (Short)`(): Unit =
    runTest {
      Structured.schema<Short>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }

  @Test
  fun `well-known (UShort)`(): Unit =
    runTest {
      Structured.schema<UShort>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonIntegerSchema.builder().build())
          }.build(),
        )
    }
}
