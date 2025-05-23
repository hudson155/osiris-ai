package osiris.core

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier

private val geminiApiKey: String =
  requireNotNull(DefaultEnvironmentVariableSupplier["GEMINI_API_KEY"]) {
    "GEMINI_API_KEY environment variable must be set."
  }

private val openAiApiKey: String =
  requireNotNull(DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]) {
    "OPEN_AI_API_KEY environment variable must be set."
  }

internal val OsirisModel.Companion.gemini20Flash: OsirisModel by lazy {
  return@lazy OsirisModel(
    name = "Gemini 2.0 Flash",
    model = GoogleAiGeminiChatModel.builder()
      .modelName("gemini-2.0-flash")
      .apiKey(geminiApiKey)
      .build(),
  )
}

internal val OsirisModel.Companion.openAiGpt41Mini: OsirisModel by lazy {
  return@lazy OsirisModel(
    name = "OpenAI GPT-4.1 mini",
    model = OpenAiChatModel.builder()
      .modelName("gpt-4.1-mini")
      .apiKey(openAiApiKey)
      .build(),
  )
}

internal val OsirisModel.Companion.openAiO3Mini: OsirisModel by lazy {
  return@lazy OsirisModel(
    name = "OpenAI o3-mini",
    model = OpenAiChatModel.builder()
      .modelName("o3-mini")
      .apiKey(openAiApiKey)
      .build(),
  )
}
