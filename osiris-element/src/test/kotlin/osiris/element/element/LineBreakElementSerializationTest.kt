package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class LineBreakElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = LineBreakElement
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "LineBreak"
          }
        """.trimIndent(),
      )
    }
}
