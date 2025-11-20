package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.CodeBlockElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser

internal class FencedCodeBlockElementParserTest : ElementParserTest() {
  @Test
  fun `code block, backticks`() {
    runTest {
      val string =
        """
          ```
          My code block

          More LOC
          ```
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        CodeBlockElement(
          language = null,
          elements = listOf(TextElement("My code block\n\nMore LOC")),
        ),
      )
    }
  }

  @Test
  fun `code block, backticks with language`() {
    runTest {
      val string =
        """
          ```kotlin
          My code block

          More LOC
          ```
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        CodeBlockElement(
          language = "kotlin",
          elements = listOf(TextElement("My code block\n\nMore LOC")),
        ),
      )
    }
  }
}
