package osiris.core

import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import osiris.core2.ModelFactory
import osiris.core2.modelFactory
import osiris.openAi.openAiApiKey

@Suppress("UnnecessaryLet")
@OptIn(ProtectedString.Access::class)
internal val testModelFactory: ModelFactory =
  modelFactory {
    openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
  }
