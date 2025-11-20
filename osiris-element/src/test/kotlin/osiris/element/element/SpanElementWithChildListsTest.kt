package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class SpanElementWithChildListsTest : ElementWithChildListsTest() {
  @Test
  fun idempotent(): Unit =
    runTest {
      val element = SpanElement(
        elements = listOf(
          TextElement("Hi, my name is Jeff."),
        ),
      )
      element.withChildLists(element.childLists)
        .shouldBe(element)
    }

  @Test
  fun new(): Unit =
    runTest {
      val element = SpanElement(
        elements = listOf(
          TextElement("Hi, my name is Jeff."),
        ),
      )
      element.withChildLists(
        listOf(
          listOf(
            TextElement("Completely new text."),
          ),
        ),
      ).shouldBe(
        SpanElement(
          elements = listOf(
            TextElement("Completely new text."),
          ),
        ),
      )
    }
}
