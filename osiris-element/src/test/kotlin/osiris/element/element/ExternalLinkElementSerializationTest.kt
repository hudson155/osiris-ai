package osiris.element.element

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ExternalLinkElementSerializationTest : ElementSerializationTest() {
  @Test
  fun test(): Unit =
    runTest {
      val element = ExternalLinkElement(content = "Airborne website", href = "https://airborne.software/")
      json.encodeToString<Element>(element).shouldBe(
        """
          {
            "type": "ExternalLink",
            "content": "Airborne website",
            "href": "https://airborne.software/"
          }
        """.trimIndent(),
      )
    }
}
