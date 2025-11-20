package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.LineBreakElement
import osiris.element.element.ParagraphElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser

internal class HardLineBreakElementParserTest : ElementParserTest() {
  @Test
  fun `hard line break`() {
    runTest {
      val string =
        """
        First line\
        Second line
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("First line"),
            LineBreakElement,
            TextElement("Second line"),
          ),
        ),
      )
    }
  }
}
