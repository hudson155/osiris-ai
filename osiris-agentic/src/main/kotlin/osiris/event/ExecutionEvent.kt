package osiris.event

public sealed class ExecutionEvent : Event() {
  public data object Start : ExecutionEvent()

  public data object End : ExecutionEvent()
}
