package osiris.core

import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import osiris.openAi.openAiApiKey

@Suppress("UnnecessaryLet")
@OptIn(ProtectedString.Access::class)
internal val modelFactory: ModelFactory =
  modelFactory {
    openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
  }
