package osiris.agentic

import osiris.openAi.openAi
import osiris.prompt.fromList

internal val ecommerceChatbot: Agent =
  agent("ecommerce_chatbot") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = ecommerceInstructionsBuilder.fromList {
      """
        # Your role and task
        
        You are the store's really smart AI assistant.
        Your task is to use tools to comprehensively answer the user's question.
      """.trimIndent()
    }
    tools += Consult("ecommerce_order_tracker")
  }
