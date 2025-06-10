package osiris.agentic

import osiris.event.Event

public fun interface Listener {
  public fun create(): (event: Event) -> Unit
}
