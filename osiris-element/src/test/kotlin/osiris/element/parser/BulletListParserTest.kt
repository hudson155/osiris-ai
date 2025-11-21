package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.BulletedListElement
import osiris.element.element.ParagraphElement

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
            listOf(ParagraphElement.text("first")),
            listOf(ParagraphElement.text("second")),
            listOf(ParagraphElement.text("third")),
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
              ParagraphElement.text("1"),
              BulletedListElement(listOf(listOf(ParagraphElement.text("2")))),
            ),
            listOf(
              ParagraphElement.text("3"),
              BulletedListElement(listOf(listOf(ParagraphElement.text("4")))),
            ),
          ),
        ),
        BulletedListElement(
          listOf(
            listOf(ParagraphElement.text("5")),
            listOf(ParagraphElement.text("6")),
          ),
        ),
      )
    }
  }
}
