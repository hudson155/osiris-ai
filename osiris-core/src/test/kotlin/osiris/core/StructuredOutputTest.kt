package osiris.core

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.openAi.openAi

internal class StructuredOutputTest {
  @Test
  fun test(): Unit = runTest {
    val response = llm(
      model = testModelFactory.openAi("gpt-4.1-nano"),
      responseType = Person::class,
      messages = listOf(
        UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
        SystemMessage("Provide a JSON representation of the person matching this description."),
      ),
    )
    response.get().convert<Person>().shouldBe(Person(name = "Jeff Hudson", age = 29))
  }
}
