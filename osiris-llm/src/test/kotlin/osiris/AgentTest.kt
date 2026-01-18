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
            defaultModel = get<ModelFactory>().openAi("gpt-5.2")
          }
        }
        single {
          modelFactory {
            @OptIn(ProtectedString.Access::class)
            openAiApiKey = ProtectedString(System.getenv("OPEN_AI_API_KEY"))
          }
        }
      },
    )
  }
}
