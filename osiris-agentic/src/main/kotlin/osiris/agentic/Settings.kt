package osiris.agentic

internal data class Settings(
  val includeConsultationProgressUpdates: Boolean,
  val persistSystemMessages: Boolean,
)

public class SettingsBuilder internal constructor() {
  public var includeConsultationProgressUpdates: Boolean = true
  public var persistSystemMessages: Boolean = false

  internal fun build(): Settings =
    Settings(
      includeConsultationProgressUpdates = includeConsultationProgressUpdates,
      persistSystemMessages = persistSystemMessages,
    )
}
