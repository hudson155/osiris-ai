package osiris.testing

public sealed class OsirisEval<out Response : Any> {
  public data class Criteria(val criteria: String) : OsirisEval<Nothing>()

  public data class Equality<out Response : Any>(val expected: Response) : OsirisEval<Response>()
}
