package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.HeadingElement
import osiris.element.element.ParagraphElement

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
        HeadingElement(level = 1, content = "**_Heading 1_**"),
        ParagraphElement.text("Foo"),
        HeadingElement(level = 2, content = "Heading 2"),
        ParagraphElement.text("Bar"),
      )
    }
  }
}
