package osiris.core

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi
import osiris.schema.OsirisSchema
import osiris.testing.verifyMessages
import osiris.testing.verifyResponse

internal class StructuredOutputTest {
  @OsirisSchema.SchemaName("person")
  internal data class Person(
    val name: String,
    val age: Int,
  )

  @Test
  fun `structured output`(): Unit = runTest {
    val osirisEvents = osiris(
      model = modelFactory.openAi("gpt-4.1-nano"),
      responseType = Person::class,
      messages = listOf(
        UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
        SystemMessage("Provide a JSON representation of the person matching this description."),
      ),
    ).toList()
    verifyMessages(osirisEvents.getMessages()) {
      verifyResponse()
    }
    osirisEvents.getMessages().getResponseAs<Person>()
      .shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
