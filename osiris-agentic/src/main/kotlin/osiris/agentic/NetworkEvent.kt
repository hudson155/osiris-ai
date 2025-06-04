package osiris.agentic

public sealed class NetworkEvent {
  public data object Start : NetworkEvent()

  public data object End : NetworkEvent()
}
