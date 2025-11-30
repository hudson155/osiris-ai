package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class SpanElementSerializationTest : ElementSerializationTest() {
  @Test
  fun default(): Unit =
    runTest {
      val element = SpanElement(
        elements = listOf(
          TextElement("Hi, my name is Jeff."),
        ),
      )
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "Span",
            "elements": [
              {
                "type": "Text",
                "content": "Hi, my name is Jeff."
              }
            ],
            "bold": false,
            "italic": false
          }
        """.trimIndent(),
      )
    }

  @Test
  fun bold(): Unit =
    runTest {
      val element = SpanElement(
        elements = listOf(
          TextElement("Hi, my name is Jeff."),
        ),
        bold = true,
      )
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "Span",
            "elements": [
              {
                "type": "Text",
                "content": "Hi, my name is Jeff."
              }
            ],
            "bold": true,
            "italic": false
          }
        """.trimIndent(),
      )
    }

  @Test
  fun italic(): Unit =
    runTest {
      val element = SpanElement(
        elements = listOf(
          TextElement("Hi, my name is Jeff."),
        ),
        italic = true,
      )
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "Span",
            "elements": [
              {
                "type": "Text",
                "content": "Hi, my name is Jeff."
              }
            ],
            "bold": false,
            "italic": true
          }
        """.trimIndent(),
      )
    }
}
