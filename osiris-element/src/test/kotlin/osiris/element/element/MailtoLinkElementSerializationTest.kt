package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class MailtoLinkElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = MailtoLinkElement(content = "support@highbeam.co", href = "support@highbeam.co")
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "MailtoLink",
            "content": "support@highbeam.co",
            "href": "support@highbeam.co"
          }
        """.trimIndent(),
      )
    }
}
