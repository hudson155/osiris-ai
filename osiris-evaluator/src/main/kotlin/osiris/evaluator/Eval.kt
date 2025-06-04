package osiris.evaluator

import osiris.schema.LlmSchema

@LlmSchema.SchemaName("eval")
internal data class Eval(
  val matchesCriteria: Boolean,
  @LlmSchema.Description("If the response does not match the criteria, provide the reason.")
  val failureReason: String?,
)
