package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class NumberedListElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = NumberedListElement(
        listOf(
          listOf(
            ParagraphElement.text("1"),
            NumberedListElement(listOf(listOf(ParagraphElement.text("2")))),
          ),
          listOf(
            ParagraphElement.text("3"),
            NumberedListElement(listOf(listOf(ParagraphElement.text("4")))),
          ),
        ),
      )
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "NumberedList",
            "items": [
              [
                {
                  "type": "Paragraph",
                  "elements": [
                    {
                      "type": "Text",
                      "content": "1"
                    }
                  ]
                },
                {
                  "type": "NumberedList",
                  "items": [
                    [
                      {
                        "type": "Paragraph",
                        "elements": [
                          {
                            "type": "Text",
                            "content": "2"
                          }
                        ]
                      }
                    ]
                  ]
                }
              ],
              [
                {
                  "type": "Paragraph",
                  "elements": [
                    {
                      "type": "Text",
                      "content": "3"
                    }
                  ]
                },
                {
                  "type": "NumberedList",
                  "items": [
                    [
                      {
                        "type": "Paragraph",
                        "elements": [
                          {
                            "type": "Text",
                            "content": "4"
                          }
                        ]
                      }
                    ]
                  ]
                }
              ]
            ]
          }
        """.trimIndent(),
      )
    }
}
