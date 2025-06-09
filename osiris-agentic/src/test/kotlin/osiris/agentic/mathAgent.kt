package osiris.agentic

import osiris.openAi.openAi

internal val mathAgent: Agent =
  agent("math_agent") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = Instructions { "Do the math. Return only the answer (nothing else)." }
  }
