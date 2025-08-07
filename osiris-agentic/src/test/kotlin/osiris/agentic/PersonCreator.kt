package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import kairo.reflect.KairoType
import kairo.reflect.kairoType
import osiris.openAi.openAi
import osiris.prompt.Instructions

internal object PersonCreator : Agent("person_creator") {
  override val responseType: KairoType<Person> = kairoType<Person>()

  override suspend fun model(): ChatModel =
    testModelFactory.openAi("gpt-5-nano") {
      temperature(0.20)
    }

  override suspend fun instructions(): Instructions =
    Instructions { "Provide a JSON representation of the person matching this description." }
}
