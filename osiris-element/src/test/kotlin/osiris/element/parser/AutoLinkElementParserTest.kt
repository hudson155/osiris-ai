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
          An auto-link has no special syntax but https://airborne.software/ just shows up somewhere.
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("An auto-link has no special syntax but "),
            ExternalLinkElement(content = "https://airborne.software/", href = "https://airborne.software/"),
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
        ParagraphElement.text("Relative links like /foo/bar?baz=qux should NOT be detected."),
      )
    }
  }
}
