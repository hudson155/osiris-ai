package osiris.osiris

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import osiris.osiris.responseConverter.JsonResponseType
import osiris.osiris.schema.OsirisSchema

internal class PersonConstructorOsirisTest : OsirisTest<PersonConstructorOsirisTest.Person>() {
  @OsirisSchema.Name("person")
  internal data class Person(
    @OsirisSchema.Type("string")
    val name: String,
    @OsirisSchema.Type("integer")
    val age: Int,
  ) {
    companion object : JsonResponseType<Person>()
  }

  override val targetModels: List<OsirisModel> =
    listOf(OsirisTestModel.gemini20Flash, OsirisTestModel.openAiGpt41Mini)

  override val evalModel: OsirisModel = OsirisTestModel.openAiO3Mini

  override val testMessages: List<OsirisTestMessage<Person>> =
    listOf(
      OsirisTestMessage(
        name = "Jeff Hudson is 29",
        request = ChatRequest.builder()
          .messages(
            SystemMessage("Provide a JSON representation of the person matching this description."),
            UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner.")
          )
          .responseFormat(Person.format())
          .build(),
        evals = listOf(
          OsirisEval.Equality(Person("Jeff Hudson", 29))
        ),
      ),
    )

  override fun buildOsiris(model: OsirisModel): Osiris<Person> =
    Osiris.create(model) {
      responseType = Person
    }
}
