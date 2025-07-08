package osiris.agentic

import kairo.reflect.kairoType
import osiris.openAi.openAi
import osiris.prompt.Instructions

internal val personCreator: Agent =
  agent("person_creator") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = Instructions { "Provide a JSON representation of the person matching this description." }
    responseType = kairoType<Person>()
  }
