package osiris.element.parser

import osiris.element.element.HeadingElement
import osiris.element.element.ParagraphElement
import osiris.element.parser.MarkdownParser
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class HeadingParserTest : ElementParserTest() {
  @Test
  fun test() {
    runTest {
      val string =
        """
        # **_Heading 1_**
        Foo
        ## Heading 2
        Bar
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        HeadingElement(level = 1, text = "**_Heading 1_**"),
        ParagraphElement.plaintext("Foo"),
        HeadingElement(level = 2, text = "Heading 2"),
        ParagraphElement.plaintext("Bar"),
      )
    }
  }
}
