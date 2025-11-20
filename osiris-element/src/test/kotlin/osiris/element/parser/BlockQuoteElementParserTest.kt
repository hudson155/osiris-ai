package osiris.element.parser

import osiris.element.element.BlockQuoteElement
import osiris.element.element.ParagraphElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class BlockQuoteElementParserTest : ElementParserTest() {
  @Test
  fun test() {
    runTest {
      val string =
        """
        Here's a block quote:
        > Never eat
        > shredded wheat
        >
        > Please
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement.plaintext("Here's a block quote:"),
        BlockQuoteElement(
          listOf(
            ParagraphElement(
              listOf(
                TextElement("Never eat"),
                TextElement(" "),
                TextElement("shredded wheat"),
              ),
            ),
            ParagraphElement.plaintext("Please"),
          ),
        ),
      )
    }
  }
}
