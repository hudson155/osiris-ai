package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class CodeBlockElementWithChildListsTest : ElementWithChildListsTest() {
  @Test
  fun `idempotent (no language)`(): Unit =
    runTest {
      val element = CodeBlockElement(
        language = null,
        elements = listOf(TextElement("My code block\n\nMore LOC")),
      )
      element.withChildLists(element.childLists)
        .shouldBe(element)
    }

  @Test
  fun `idempotent (with language)`(): Unit =
    runTest {
      val element = CodeBlockElement(
        language = "kotlin",
        elements = listOf(TextElement("My code block\n\nMore LOC")),
      )
      element.withChildLists(element.childLists)
        .shouldBe(element)
    }

  @Test
  fun `new (no language)`(): Unit =
    runTest {
      val element = CodeBlockElement(
        language = null,
        elements = listOf(TextElement("My code block\n\nMore LOC")),
      )
      element.withChildLists(
        listOf(
          listOf(TextElement("Some brand new code!")),
        ),
      ).shouldBe(
        CodeBlockElement(
          language = null,
          elements = listOf(TextElement("Some brand new code!")),
        ),
      )
    }

  @Test
  fun `new (with language)`(): Unit =
    runTest {
      val element = CodeBlockElement(
        language = "kotlin",
        elements = listOf(TextElement("My code block\n\nMore LOC")),
      )
      element.withChildLists(
        listOf(
          listOf(TextElement("Some brand new code!")),
        ),
      ).shouldBe(
        CodeBlockElement(
          language = "kotlin",
          elements = listOf(TextElement("Some brand new code!")),
        ),
      )
    }
}
