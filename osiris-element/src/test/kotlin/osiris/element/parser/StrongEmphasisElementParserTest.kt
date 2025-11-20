package osiris.element.parser

import osiris.element.element.ParagraphElement
import osiris.element.element.SpanElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StrongEmphasisElementParserTest {
  @Test
  fun `bold text`() {
    runTest {
      val string =
        """
        Some text is **bolded** in __different ways__.
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("Some text is "),
            SpanElement(
              elements = listOf(TextElement("bolded")),
              bold = true,
            ),
            TextElement(" in "),
            SpanElement(
              elements = listOf(TextElement("different ways")),
              bold = true,
            ),
            TextElement("."),
          ),
        ),
      )
    }
  }
}
