package osiris.agentic

import osiris.openAi.openAi

internal val ecommerceOrderTracker: Agent =
  agent("ecommerce_order_tracker") {
    description = "Use to track an order."
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = ecommerceInstructionsBuilder.create {
      """
        # Your role and task
        
        You are the store's data analyst.
        Your role is to track orders.
      """.trimIndent()
    }
    tools += TrackOrderTool
  }
