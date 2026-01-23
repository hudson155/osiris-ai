package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.CodeElement
import osiris.element.element.ParagraphElement
import osiris.element.element.TextElement

internal class CodeElementParserTest : ElementParserTest() {
  @Test
  fun `code (inline)`() {
    runTest {
      val string =
        """
          Here's some `inline code`!
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("Here's some "),
            CodeElement("inline code"),
            TextElement("!"),
          ),
        ),
      )
    }
  }
}
