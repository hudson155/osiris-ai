package osiris

import kairo.dependencyInjection.KoinExtension
import kairo.protectedString.ProtectedString
import org.junit.jupiter.api.extension.ExtensionContext
import org.koin.dsl.module
import org.koin.ksp.generated.module

internal class AgentTest : KoinExtension() {
  @OptIn(ProtectedString.Access::class)
  override fun beforeEach(context: ExtensionContext) {
    super.beforeEach(context)
    val koin = checkNotNull(context.koin)
    koin.modules(
      TestModule.module,
      module {
        factory {
          context {
            defaultModel = get<ModelFactory>().anthropic("claude-opus-4-6") {
              beta("structured-outputs-2025-11-13")
            }
          }
        }
        single {
          modelFactory {
            @OptIn(ProtectedString.Access::class)
            anthropicApiKey = ProtectedString(System.getenv("ANTHROPIC_API_KEY"))
          }
        }
      },
    )
  }
}
