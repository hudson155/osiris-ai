package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonSchema
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Includes all time-related types that kairo-serialization supports.
 */
internal class StructuredTimeTest {
  @Test
  fun `well-known (Java DayOfWeek)`(): Unit =
    runTest {
      Structured.schema<java.time.DayOfWeek>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin DayOfWeek)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.DayOfWeek>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java Duration)`(): Unit =
    runTest {
      Structured.schema<java.time.Duration>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin Duration)`(): Unit =
    runTest {
      Structured.schema<kotlin.time.Duration>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java Instant)`(): Unit =
    runTest {
      Structured.schema<java.time.Instant>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin Instant)`(): Unit =
    runTest {
      Structured.schema<kotlin.time.Instant>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java LocalDateTime)`(): Unit =
    runTest {
      Structured.schema<java.time.LocalDateTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin LocalDateTime)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.LocalDateTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java LocalDate)`(): Unit =
    runTest {
      Structured.schema<java.time.LocalDate>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin LocalDate)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.LocalDate>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java LocalTime)`(): Unit =
    runTest {
      Structured.schema<java.time.LocalTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin LocalTime)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.LocalTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java MonthDay)`(): Unit =
    runTest {
      Structured.schema<java.time.MonthDay>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java Month)`(): Unit =
    runTest {
      Structured.schema<java.time.Month>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin Month)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.Month>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java OffsetDateTime)`(): Unit =
    runTest {
      Structured.schema<java.time.OffsetDateTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java OffsetTime)`(): Unit =
    runTest {
      Structured.schema<java.time.OffsetTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java Period)`(): Unit =
    runTest {
      Structured.schema<java.time.Period>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin DatePeriod)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.DatePeriod>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java YearMonth)`(): Unit =
    runTest {
      Structured.schema<java.time.YearMonth>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin YearMonth)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.YearMonth>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java Year)`(): Unit =
    runTest {
      Structured.schema<java.time.Year>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java ZonedDateTime)`(): Unit =
    runTest {
      Structured.schema<java.time.ZonedDateTime>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java ZoneId)`(): Unit =
    runTest {
      Structured.schema<java.time.ZoneId>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin TimeZone)`(): Unit =
    runTest {
      Structured.schema<kotlinx.datetime.TimeZone>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Java ZoneOffset)`(): Unit =
    runTest {
      Structured.schema<java.time.ZoneOffset>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }

  @Test
  fun `well-known (Kotlin FixedOffsetTimeZone)`(): Unit =
    runTest {
      Structured.schema<java.time.ZoneOffset>("schema")
        .shouldBe(
          JsonSchema.builder().apply {
            name("schema")
            rootElement(JsonStringSchema.builder().build())
          }.build()
        )
    }
}
