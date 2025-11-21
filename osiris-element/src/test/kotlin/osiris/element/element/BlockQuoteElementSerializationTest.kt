package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class BlockQuoteElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
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
          ParagraphElement.text("Please"),
        ),
      )
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "BlockQuote",
            "elements": [
              {
                "type": "Paragraph",
                "elements": [
                  {
                    "type": "Text",
                    "content": "Never eat"
                  },
                  {
                    "type": "Text",
                    "content": " "
                  },
                  {
                    "type": "Text",
                    "content": "shredded wheat"
                  }
                ]
              },
              {
                "type": "Paragraph",
                "elements": [
                  {
                    "type": "Text",
                    "content": "Please"
                  }
                ]
              }
            ]
          }
        """.trimIndent(),
      )
    }
}
