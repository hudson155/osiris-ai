package osiris.agentic

import osiris.openAi.openAi

internal val weatherAgent: Agent =
  agent("weather_agent") {
    model = testModelFactory.openAi("gpt-4.1-nano") {
      temperature(0.20)
    }
    instructions = "Do the math. Return only the answer (nothing else)."
    tools += WeatherTool
  }
