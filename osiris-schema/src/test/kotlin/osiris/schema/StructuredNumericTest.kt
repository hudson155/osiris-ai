package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema
import dev.langchain4j.model.chat.request.json.JsonNullSchema
import dev.langchain4j.model.chat.request.json.JsonNumberSchema
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
      Structured.generate<Long?>()
        .shouldBe(
          JsonAnyOfSchema.builder().apply {
            anyOf(JsonIntegerSchema.builder().build(), JsonNullSchema)
          }.build()
        )
    }

  @Test
  fun `nullable (Number)`(): Unit =
    runTest {
      Structured.generate<Double?>()
        .shouldBe(
          JsonAnyOfSchema.builder().apply {
            anyOf(JsonNumberSchema.builder().build(), JsonNullSchema)
          }.build()
        )
    }

  @Test
  fun `with description (Integer)`(): Unit =
    runTest {
      Structured.generate(
        Long::class.createType(
          annotations = listOf(Structured.Description("An integer"))
        )
      ).shouldBe(
        JsonIntegerSchema.builder().apply {
          description("An integer")
        }.build(),
      )
    }

  @Test
  fun `with description (Number)`(): Unit =
    runTest {
      Structured.generate(
        Double::class.createType(
          annotations = listOf(Structured.Description("A number"))
        )
      ).shouldBe(
        JsonNumberSchema.builder().apply {
          description("A number")
        }.build(),
      )
    }

  @Test
  fun `explicit (Unit to Integer)`(): Unit =
    runTest {
      Structured.generate(
        Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.Integer))
        )
      ).shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `explicit (Unit to Number)`(): Unit =
    runTest {
      Structured.generate(
        Unit::class.createType(
          annotations = listOf(Structured.Type(StructureType.Number))
        )
      ).shouldBe(JsonNumberSchema.builder().build())
    }

  @Test
  fun `well-known (BigDecimal)`(): Unit =
    runTest {
      Structured.generate<BigDecimal>()
        .shouldBe(JsonNumberSchema.builder().build())
    }

  @Test
  fun `well-known (BigInteger)`(): Unit =
    runTest {
      Structured.generate<BigInteger>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (Byte)`(): Unit =
    runTest {
      Structured.generate<Byte>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (UByte)`(): Unit =
    runTest {
      Structured.generate<UByte>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (Double)`(): Unit =
    runTest {
      Structured.generate<Double>()
        .shouldBe(JsonNumberSchema.builder().build())
    }

  @Test
  fun `well-known (Float)`(): Unit =
    runTest {
      Structured.generate<Float>()
        .shouldBe(JsonNumberSchema.builder().build())
    }

  @Test
  fun `well-known (Int)`(): Unit =
    runTest {
      Structured.generate<Int>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (UInt)`(): Unit =
    runTest {
      Structured.generate<UInt>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (Long)`(): Unit =
    runTest {
      Structured.generate<Long>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (ULong)`(): Unit =
    runTest {
      Structured.generate<ULong>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (Short)`(): Unit =
    runTest {
      Structured.generate<Short>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }

  @Test
  fun `well-known (UShort)`(): Unit =
    runTest {
      Structured.generate<UShort>()
        .shouldBe(JsonIntegerSchema.builder().build())
    }
}
