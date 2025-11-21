package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class MailtoLinkElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = MailtoLinkElement(content = "jeff@example.com", href = "jeff@example.com")
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "MailtoLink",
            "content": "jeff@example.com",
            "href": "jeff@example.com"
          }
        """.trimIndent(),
      )
    }
}
