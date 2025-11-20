package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.MailtoLinkElement
import osiris.element.element.ParagraphElement

internal class MailLinkElementParserTest : ElementParserTest() {
  @Test
  fun test() {
    runTest {
      val string =
        """
          support@highbeam.co
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(MailtoLinkElement(content = "support@highbeam.co", href = "support@highbeam.co")),
        ),
      )
    }
  }
}
