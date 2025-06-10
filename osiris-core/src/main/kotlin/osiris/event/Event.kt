package osiris.event

import java.time.Instant

public abstract class Event {
  public abstract val at: Instant
}
