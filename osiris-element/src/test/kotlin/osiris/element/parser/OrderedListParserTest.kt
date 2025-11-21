package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.NumberedListElement
import osiris.element.element.ParagraphElement

internal class OrderedListParserTest : ElementParserTest() {
  @Test
  fun `single list with dots`() {
    runTest {
      val string =
        """
          1. first
            3. second
              2. third
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        NumberedListElement(
          listOf(
            listOf(ParagraphElement.text("first")),
            listOf(ParagraphElement.text("second")),
            listOf(ParagraphElement.text("third")),
          ),
        ),
      )
    }
  }

  @Test
  fun `single list with parens`() {
    runTest {
      val string =
        """
          1) first
            1) second
              1) third
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        NumberedListElement(
          listOf(
            listOf(ParagraphElement.text("first")),
            listOf(ParagraphElement.text("second")),
            listOf(ParagraphElement.text("third")),
          ),
        ),
      )
    }
  }

  @Test
  fun `nested lists`() {
    runTest {
      val string =
        """
            1. 1
                2. 2
              3. 3
                  4) 4
                5) 5
          6) 6
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        NumberedListElement(
          listOf(
            listOf(
              ParagraphElement.text("1"),
              NumberedListElement(listOf(listOf(ParagraphElement.text("2")))),
            ),
            listOf(
              ParagraphElement.text("3"),
              NumberedListElement(listOf(listOf(ParagraphElement.text("4")))),
            ),
          ),
        ),
        NumberedListElement(
          listOf(
            listOf(ParagraphElement.text("5")),
            listOf(ParagraphElement.text("6")),
          ),
        ),
      )
    }
  }
}
