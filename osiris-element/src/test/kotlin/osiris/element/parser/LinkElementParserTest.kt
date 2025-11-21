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
          [Airborne website](https://airborne.software/)
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(ExternalLinkElement(content = "Airborne website", href = "https://airborne.software/")),
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
