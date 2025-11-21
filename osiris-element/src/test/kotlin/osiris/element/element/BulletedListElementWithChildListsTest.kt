package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class BulletedListElementWithChildListsTest : ElementWithChildListsTest() {
  @Test
  fun idempotent(): Unit =
    runTest {
      val element = BulletedListElement(
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
      )
      element.withChildLists(element.childLists)
        .shouldBe(element)
    }

  @Test
  fun new(): Unit =
    runTest {
      val element = BulletedListElement(
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
      )
      element.withChildLists(
        listOf(
          listOf(
            ParagraphElement.text("4"),
            BulletedListElement(listOf(listOf(ParagraphElement.text("5")))),
          ),
          listOf(
            ParagraphElement.text("6"),
            BulletedListElement(listOf(listOf(ParagraphElement.text("7")))),
          ),
        ),
      ).shouldBe(
        BulletedListElement(
          listOf(
            listOf(
              ParagraphElement.text("4"),
              BulletedListElement(listOf(listOf(ParagraphElement.text("5")))),
            ),
            listOf(
              ParagraphElement.text("6"),
              BulletedListElement(listOf(listOf(ParagraphElement.text("7")))),
            ),
          ),
        ),
      )
    }
}
