package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class CodeBlockElementSerializationTest : ElementSerializationTest() {
  @Test
  fun `no language`(): Unit =
    runTest {
      val element = CodeBlockElement(
        language = null,
        elements = listOf(TextElement("My code block\n\nMore LOC")),
      )
      json.encodeToString<Element>(element).shouldBe(
        """
        {
          "type": "CodeBlock",
          "language": null,
          "elements": [
            {
              "type": "Text",
              "content": "My code block\n\nMore LOC"
            }
          ]
        }
        """.trimIndent(),
      )
    }

  @Test
  fun `with language`(): Unit =
    runTest {
      val element = CodeBlockElement(
        language = "kotlin",
        elements = listOf(TextElement("My code block\n\nMore LOC")),
      )
      json.encodeToString<Element>(element).shouldBe(
        """
        {
          "type": "CodeBlock",
          "language": "kotlin",
          "elements": [
            {
              "type": "Text",
              "content": "My code block\n\nMore LOC"
            }
          ]
        }
        """.trimIndent(),
      )
    }
}
