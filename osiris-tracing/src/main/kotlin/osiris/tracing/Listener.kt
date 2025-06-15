package osiris.tracing

public interface Listener {
  public fun event(event: Event)

  public fun flush()
}
