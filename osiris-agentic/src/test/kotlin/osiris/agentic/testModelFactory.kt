package osiris.agentic

import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import osiris.chat.ModelFactory
import osiris.chat.modelFactory
import osiris.openAi.openAiApiKey

@Suppress("UnnecessaryLet")
@OptIn(ProtectedString.Access::class)
internal val testModelFactory: ModelFactory =
  modelFactory {
    openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
  }
