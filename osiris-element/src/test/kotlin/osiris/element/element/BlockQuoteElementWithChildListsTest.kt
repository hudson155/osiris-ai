package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class BlockQuoteElementWithChildListsTest : ElementWithChildListsTest() {
  @Test
  fun idempotent(): Unit =
    runTest {
      val element = BlockQuoteElement(
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
      )
      element.withChildLists(element.childLists)
        .shouldBe(element)
    }

  @Test
  fun new(): Unit =
    runTest {
      val element = BlockQuoteElement(
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
      )
      element.withChildLists(
        listOf(
          listOf(
            ParagraphElement(
              listOf(
                TextElement("You can sometimes eat"),
                TextElement(" "),
                TextElement("shredded wheat"),
              ),
            ),
            ParagraphElement.plaintext("Thanks"),
          ),
        ),
      ).shouldBe(
        BlockQuoteElement(
          listOf(
            ParagraphElement(
              listOf(
                TextElement("You can sometimes eat"),
                TextElement(" "),
                TextElement("shredded wheat"),
              ),
            ),
            ParagraphElement.plaintext("Thanks"),
          ),
        ),
      )
    }
}
