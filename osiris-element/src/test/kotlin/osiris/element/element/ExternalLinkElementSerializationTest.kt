package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ExternalLinkElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = ExternalLinkElement(content = "Highbeam website", href = "https://www.highbeam.co/")
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "ExternalLink",
            "content": "Highbeam website",
            "href": "https://www.highbeam.co/"
          }
        """.trimIndent(),
      )
    }
}
