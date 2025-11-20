package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ThematicBreakElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = ThematicBreakElement
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "ThematicBreak"
          }
        """.trimIndent(),
      )
    }
}
