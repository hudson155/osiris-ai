package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.ExternalLinkElement
import osiris.element.element.ParagraphElement

internal class LinkElementParserTest : ElementParserTest() {
  @Test
  fun link() {
    runTest {
      val string =
        """
          [Highbeam website](https://www.highbeam.co/)
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(ExternalLinkElement(content = "Highbeam website", href = "https://www.highbeam.co/")),
        ),
      )
    }
  }

  @Test
  fun action() {
    runTest {
      val string =
        """
          [Contact support](action://contactSupport)
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(ExternalLinkElement(content = "Contact support", href = "action://contactSupport")),
        ),
      )
    }
  }
}
