package osiris.agent

import kotlinx.coroutines.channels.Channel
import osiris.event.Event

public abstract class Context : Channel<Event> by Channel()
