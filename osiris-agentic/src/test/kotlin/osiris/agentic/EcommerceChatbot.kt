package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import osiris.chat.Tool
import osiris.openAi.openAi
import osiris.prompt.Instructions
import osiris.prompt.build

internal object EcommerceChatbot : Agent("ecommerce_chatbot") {
  override suspend fun model(): ChatModel =
    testModelFactory.openAi("gpt-5-nano")

  override suspend fun instructions(): Instructions =
    ecommerceInstructionsBuilder.build {
      """
        # Your role and task
        
        You are the store's really smart AI assistant.
        Your task is to use tools to comprehensively answer the user's question.
      """.trimIndent()
    }

  override suspend fun tools(): List<Tool<*>> =
    listOf(
      Consult("ecommerce_order_tracker"),
    )
}
