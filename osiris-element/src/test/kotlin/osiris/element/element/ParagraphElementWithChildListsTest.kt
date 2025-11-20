package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ParagraphElementWithChildListsTest : ElementWithChildListsTest() {
  @Test
  fun idempotent(): Unit =
    runTest {
      val element = ParagraphElement(
        elements = listOf(
          TextElement("A paragraph can contain "),
          SpanElement(
            elements = listOf(
              TextElement("bold text"),
            ),
            bold = true,
          ),
          TextElement(" and "),
          SpanElement(
            elements = listOf(
              TextElement("italic text"),
            ),
            italic = true,
          ),
          TextElement("."),
          LineBreakElement,
          TextElement("It can also contain line breaks."),
        ),
      )
      element.withChildLists(element.childLists)
        .shouldBe(element)
    }

  @Test
  fun new(): Unit =
    runTest {
      val element = ParagraphElement(
        elements = listOf(
          TextElement("A paragraph can contain "),
          SpanElement(
            elements = listOf(
              TextElement("bold text"),
            ),
            bold = true,
          ),
          TextElement(" and "),
          SpanElement(
            elements = listOf(
              TextElement("italic text"),
            ),
            italic = true,
          ),
          TextElement("."),
          LineBreakElement,
          TextElement("It can also contain line breaks."),
        ),
      )
      element.withChildLists(
        listOf(
          listOf(
            TextElement("Completely new text."),
            LineBreakElement,
            SpanElement(
              elements = listOf(
                TextElement("Including some bold text."),
              ),
              bold = true,
            ),
          ),
        ),
      ).shouldBe(
        ParagraphElement(
          elements = listOf(
            TextElement("Completely new text."),
            LineBreakElement,
            SpanElement(
              elements = listOf(
                TextElement("Including some bold text."),
              ),
              bold = true,
            ),
          ),
        ),
      )
    }
}
