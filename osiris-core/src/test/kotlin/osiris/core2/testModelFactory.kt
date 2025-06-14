package osiris.core2

import kairo.environmentVariableSupplier.DefaultEnvironmentVariableSupplier
import kairo.protectedString.ProtectedString
import osiris.core.ModelFactory
import osiris.core.modelFactory
import osiris.openAi.openAiApiKey

@Suppress("UnnecessaryLet")
@OptIn(ProtectedString.Access::class)
internal val testModelFactory: ModelFactory =
  modelFactory {
    openAiApiKey = DefaultEnvironmentVariableSupplier["OPEN_AI_API_KEY"]?.let { ProtectedString(it) }
  }
