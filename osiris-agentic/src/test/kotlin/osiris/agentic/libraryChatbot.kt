package osiris.agentic

import osiris.openAi.openAi

internal val libraryChatbot: Agent =
  agent("library_chatbot") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = libraryInstructions.create(
      """
        # Your role and task

        You are the library's really smart AI assistant.
        Your task is to use tools to comprehensively answer the user's question.
      """.trimIndent(),
    )
  }
