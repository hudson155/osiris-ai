package osiris.core

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import dev.langchain4j.model.openai.OpenAiChatModel
import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier

internal object OsirisTestModel {
  private val geminiApiKey: String =
    requireNotNull(DefaultEnvironmentVariableSupplier["GEMINI_API_KEY"]) {
      "GEMINI_API_KEY environment variable must be set."
    }

  private val openAiApiKey: String =
    requireNotNull(DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]) {
      "OPEN_AI_API_KEY environment variable must be set."
    }

  val gemini20Flash: ChatModel =
    GoogleAiGeminiChatModel.builder()
      .modelName("gemini-2.0-flash")
      .apiKey(geminiApiKey)
      .build()

  val openAiGpt41Mini: ChatModel =
    OpenAiChatModel.builder()
      .modelName("gpt-4.1-mini")
      .apiKey(openAiApiKey)
      .build()

  val openAiO3Mini: ChatModel =
    OpenAiChatModel.builder()
      .modelName("o3-mini")
      .apiKey(openAiApiKey)
      .build()
}
