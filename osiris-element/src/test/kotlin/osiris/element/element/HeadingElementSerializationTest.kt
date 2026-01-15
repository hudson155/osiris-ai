package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class HeadingElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = HeadingElement(level = 2, content = "Heading 2")
      json.serialize<Element>(element).shouldBe(
        """
          {
            "type": "Heading",
            "level": 2,
            "content": "Heading 2"
          }
        """.trimIndent(),
      )
    }
}
