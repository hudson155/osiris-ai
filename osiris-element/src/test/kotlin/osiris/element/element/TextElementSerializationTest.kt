package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class TextElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = TextElement("Hi, my name is Jeff.")
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "Text",
            "content": "Hi, my name is Jeff."
          }
        """.trimIndent(),
      )
    }
}
