package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.ExternalLinkElement
import osiris.element.element.ParagraphElement
import osiris.element.element.TextElement

internal class AutoLinkElementParserTest : ElementParserTest() {
  @Test
  fun `absolute link`() {
    runTest {
      val string =
        """
          An auto-link has no special syntax but https://www.highbeam.co/ just shows up somewhere.
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("An auto-link has no special syntax but "),
            ExternalLinkElement(content = "https://www.highbeam.co/", href = "https://www.highbeam.co/"),
            TextElement(" just shows up somewhere."),
          ),
        ),
      )
    }
  }

  @Test
  fun `relative link`() {
    runTest {
      val string =
        """
          Relative links like /foo/bar?baz=qux should NOT be detected.
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement.plaintext("Relative links like /foo/bar?baz=qux should NOT be detected."),
      )
    }
  }
}
