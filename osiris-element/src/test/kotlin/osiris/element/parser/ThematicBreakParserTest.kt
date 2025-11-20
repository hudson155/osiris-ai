package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.ParagraphElement
import osiris.element.element.ThematicBreakElement
import osiris.element.parser.MarkdownParser

internal class ThematicBreakParserTest : ElementParserTest() {
  @Test
  fun stars() {
    runTest {
      val string =
        """
          Some content
          ***
          Some other content
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement.plaintext("Some content"),
        ThematicBreakElement,
        ParagraphElement.plaintext("Some other content"),
      )
    }
  }

  @Test
  fun dashes() {
    runTest {
      val string =
        """
          Some content

          ---

          Some other content
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement.plaintext("Some content"),
        ThematicBreakElement,
        ParagraphElement.plaintext("Some other content"),
      )
    }
  }

  @Test
  fun underscores() {
    runTest {
      val string =
        """
          Some content
          ___
          Some other content
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement.plaintext("Some content"),
        ThematicBreakElement,
        ParagraphElement.plaintext("Some other content"),
      )
    }
  }
}
