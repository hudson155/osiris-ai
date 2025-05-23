package osiris.core.event

public class ExceptionOsirisEvent(public val e: Throwable) : TerminalOsirisEvent<Nothing>()
