package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class BulletedListElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = BulletedListElement(
        listOf(
          listOf(
            ParagraphElement.plaintext("1"),
            BulletedListElement(listOf(listOf(ParagraphElement.plaintext("2")))),
          ),
          listOf(
            ParagraphElement.plaintext("3"),
            BulletedListElement(listOf(listOf(ParagraphElement.plaintext("4")))),
          ),
        ),
      )
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "BulletedList",
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
                  "type": "BulletedList",
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
                  "type": "BulletedList",
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
