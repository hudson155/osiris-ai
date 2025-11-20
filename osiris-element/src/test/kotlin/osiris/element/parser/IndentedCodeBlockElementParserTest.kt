package osiris.element.parser

import osiris.element.element.CodeBlockElement
import osiris.element.element.ParagraphElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class IndentedCodeBlockElementParserTest : ElementParserTest() {
  @Test
  fun `code block, indented`() {
    runTest {
      val string =
        """
        Here's a code block:

            My code block
            Second line
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement.plaintext("Here's a code block:"),
        CodeBlockElement(
          language = null,
          elements = listOf(TextElement("My code block\nSecond line")),
        ),
      )
    }
  }
}
