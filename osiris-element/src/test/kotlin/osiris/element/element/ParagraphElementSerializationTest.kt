package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ParagraphElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = ParagraphElement(
        elements = listOf(
          TextElement("A paragraph can contain "),
          SpanElement(
            elements = listOf(
              TextElement("bold text"),
            ),
            bold = true,
          ),
          TextElement(" and "),
          SpanElement(
            elements = listOf(
              TextElement("italic text"),
            ),
            italic = true,
          ),
          TextElement("."),
          LineBreakElement,
          TextElement("It can also contain line breaks."),
        ),
      )
      json.serialize<Element>(element).shouldBe(
        """
          {
            "type": "Paragraph",
            "elements": [
              {
                "type": "Text",
                "content": "A paragraph can contain "
              },
              {
                "type": "Span",
                "elements": [
                  {
                    "type": "Text",
                    "content": "bold text"
                  }
                ],
                "bold": true,
                "italic": false
              },
              {
                "type": "Text",
                "content": " and "
              },
              {
                "type": "Span",
                "elements": [
                  {
                    "type": "Text",
                    "content": "italic text"
                  }
                ],
                "bold": false,
                "italic": true
              },
              {
                "type": "Text",
                "content": "."
              },
              {
                "type": "LineBreak"
              },
              {
                "type": "Text",
                "content": "It can also contain line breaks."
              }
            ]
          }
        """.trimIndent(),
      )
    }
}
