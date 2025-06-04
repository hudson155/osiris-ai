package osiris.agentic

internal data class Settings(
  val persistSystemMessages: Boolean,
)

public class SettingsBuilder internal constructor() {
  public var persistSystemMessages: Boolean = false

  internal fun build(): Settings =
    Settings(
      persistSystemMessages = persistSystemMessages,
    )
}
