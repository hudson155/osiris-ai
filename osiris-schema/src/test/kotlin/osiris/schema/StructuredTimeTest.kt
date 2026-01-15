package osiris.schema

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
      Structured.generate<java.time.DayOfWeek>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin DayOfWeek)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.DayOfWeek>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java Duration)`(): Unit =
    runTest {
      Structured.generate<java.time.Duration>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin Duration)`(): Unit =
    runTest {
      Structured.generate<kotlin.time.Duration>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java Instant)`(): Unit =
    runTest {
      Structured.generate<java.time.Instant>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin Instant)`(): Unit =
    runTest {
      Structured.generate<kotlin.time.Instant>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java LocalDateTime)`(): Unit =
    runTest {
      Structured.generate<java.time.LocalDateTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin LocalDateTime)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.LocalDateTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java LocalDate)`(): Unit =
    runTest {
      Structured.generate<java.time.LocalDate>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin LocalDate)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.LocalDate>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java LocalTime)`(): Unit =
    runTest {
      Structured.generate<java.time.LocalTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin LocalTime)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.LocalTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java MonthDay)`(): Unit =
    runTest {
      Structured.generate<java.time.MonthDay>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java Month)`(): Unit =
    runTest {
      Structured.generate<java.time.Month>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin Month)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.Month>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java OffsetDateTime)`(): Unit =
    runTest {
      Structured.generate<java.time.OffsetDateTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java OffsetTime)`(): Unit =
    runTest {
      Structured.generate<java.time.OffsetTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java Period)`(): Unit =
    runTest {
      Structured.generate<java.time.Period>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin DatePeriod)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.DatePeriod>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java YearMonth)`(): Unit =
    runTest {
      Structured.generate<java.time.YearMonth>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin YearMonth)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.YearMonth>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java Year)`(): Unit =
    runTest {
      Structured.generate<java.time.Year>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java ZonedDateTime)`(): Unit =
    runTest {
      Structured.generate<java.time.ZonedDateTime>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java ZoneId)`(): Unit =
    runTest {
      Structured.generate<java.time.ZoneId>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin TimeZone)`(): Unit =
    runTest {
      Structured.generate<kotlinx.datetime.TimeZone>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Java ZoneOffset)`(): Unit =
    runTest {
      Structured.generate<java.time.ZoneOffset>()
        .shouldBe(JsonStringSchema.builder().build())
    }

  @Test
  fun `well-known (Kotlin FixedOffsetTimeZone)`(): Unit =
    runTest {
      Structured.generate<java.time.ZoneOffset>()
        .shouldBe(JsonStringSchema.builder().build())
    }
}
