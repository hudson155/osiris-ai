package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class CodeElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = CodeElement("inline code")
      json.serialize<Element>(element).shouldBe(
        """
          {
            "type": "Code",
            "content": "inline code"
          }
        """.trimIndent(),
      )
    }
}
