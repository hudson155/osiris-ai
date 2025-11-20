package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.ParagraphElement
import osiris.element.element.SpanElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser

internal class EmphasisElementParserTest : ElementParserTest() {
  @Test
  fun `italic text`() {
    runTest {
      val string =
        """
        Some text is *italicized* in _different ways_.
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("Some text is "),
            SpanElement(
              elements = listOf(TextElement("italicized")),
              italic = true,
            ),
            TextElement(" in "),
            SpanElement(
              elements = listOf(TextElement("different ways")),
              italic = true,
            ),
            TextElement("."),
          ),
        ),
      )
    }
  }
}
