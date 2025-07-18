package osiris.chat

import osiris.schema.LlmSchema

@LlmSchema.SchemaName("person")
internal data class Person(
  val name: String,
  val age: Int,
)
