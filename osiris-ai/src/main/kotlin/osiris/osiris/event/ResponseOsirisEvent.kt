package osiris.osiris.event

public class ResponseOsirisEvent<out Response : Any>(
  public val content: Response,
) : TerminalOsirisEvent<Response>()
