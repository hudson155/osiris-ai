package osiris.element.parser

import osiris.element.element.NumberedListElement
import osiris.element.element.ParagraphElement
import osiris.element.parser.MarkdownParser
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

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
            listOf(ParagraphElement.plaintext("first")),
            listOf(ParagraphElement.plaintext("second")),
            listOf(ParagraphElement.plaintext("third")),
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
            listOf(ParagraphElement.plaintext("first")),
            listOf(ParagraphElement.plaintext("second")),
            listOf(ParagraphElement.plaintext("third")),
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
              ParagraphElement.plaintext("1"),
              NumberedListElement(listOf(listOf(ParagraphElement.plaintext("2")))),
            ),
            listOf(
              ParagraphElement.plaintext("3"),
              NumberedListElement(listOf(listOf(ParagraphElement.plaintext("4")))),
            ),
          ),
        ),
        NumberedListElement(
          listOf(
            listOf(ParagraphElement.plaintext("5")),
            listOf(ParagraphElement.plaintext("6")),
          ),
        ),
      )
    }
  }
}
