package osiris.element.parser

import osiris.element.element.BulletedListElement
import osiris.element.element.ParagraphElement
import osiris.element.parser.MarkdownParser
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class BulletListParserTest : ElementParserTest() {
  @Test
  fun `single list with dashes`() {
    runTest {
      val string =
        """
        - first
         - second
          - third
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        BulletedListElement(
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
  fun `single list with stars`() {
    runTest {
      val string =
        """
        * first
         * second
          * third
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        BulletedListElement(
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
         - 1
           - 2
          - 3
            * 4
           * 5
        * 6
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        BulletedListElement(
          listOf(
            listOf(
              ParagraphElement.plaintext("1"),
              BulletedListElement(listOf(listOf(ParagraphElement.plaintext("2")))),
            ),
            listOf(
              ParagraphElement.plaintext("3"),
              BulletedListElement(listOf(listOf(ParagraphElement.plaintext("4")))),
            ),
          ),
        ),
        BulletedListElement(
          listOf(
            listOf(ParagraphElement.plaintext("5")),
            listOf(ParagraphElement.plaintext("6")),
          ),
        ),
      )
    }
  }
}
