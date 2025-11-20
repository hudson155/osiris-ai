package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class HeadingElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = HeadingElement(level = 2, text = "Heading 2")
      json.encodeToString<Element>(element).shouldBe(
        """
        {
          "type": "Heading",
          "level": 2,
          "text": "Heading 2"
        }
        """.trimIndent(),
      )
    }
}
