package osiris.agentic

import dev.langchain4j.model.chat.ChatModel
import osiris.chat.Tool
import osiris.openAi.openAi
import osiris.prompt.Instructions
import osiris.prompt.build

internal object EcommerceOrderTracker : Agent("ecommerce_order_tracker") {
  override val description: String = "Use to track an order."

  override suspend fun model(): ChatModel =
    testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }

  override suspend fun instructions(): Instructions =
    ecommerceInstructionsBuilder.build {
      """
        # Your role and task
        
        You are the store's data analyst.
        Your role is to track orders.
      """.trimIndent()
    }

  override suspend fun tools(): List<Tool<*>> =
    listOf(
      TrackOrderTool,
    )
}
