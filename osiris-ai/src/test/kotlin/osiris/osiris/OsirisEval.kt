package osiris.osiris

sealed class OsirisEval<out Response : Any> {
  data class Criteria(val criteria: String) : OsirisEval<Nothing>()

  data class Equality<out Response : Any>(val expected: Response) : OsirisEval<Response>()
}
