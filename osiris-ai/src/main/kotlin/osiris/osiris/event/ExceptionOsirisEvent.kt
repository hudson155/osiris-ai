package osiris.osiris.event

public class ExceptionOsirisEvent(public val e: Throwable) : TerminalOsirisEvent<Nothing>()
