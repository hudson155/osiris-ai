package osiris.agentic

import osiris.openAi.openAi

internal val ecommerceOrderTracker: Agent =
  agent("ecommerce_order_tracker") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = ecommerceInstructions.create(
      """
        # Your role and task

        You are the store's data analyst.
        Your role is to track orders.
      """.trimIndent(),
    )
    tools += TrackOrderTool
  }
