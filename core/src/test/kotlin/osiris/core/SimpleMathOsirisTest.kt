package osiris.core

import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest

internal class SimpleMathOsirisTest : OsirisTest<String>() {
  override val targetModels: List<OsirisModel> =
    listOf(OsirisTestModel.gemini20Flash, OsirisTestModel.openAiGpt41Mini)

  override val evalModel: OsirisModel = OsirisTestModel.openAiO3Mini

  override val testMessages: List<OsirisTestMessage<String>> =
    listOf(
      OsirisTestMessage(
        name = "What's 2+2?",
        request = ChatRequest.builder()
          .messages(UserMessage("What's 2+2?"))
          .build(),
        evals = listOf(
          OsirisEval.Criteria("Should say the answer is 4."),
        ),
      ),
    )

  override fun buildOsiris(model: OsirisModel): Osiris<String> =
    Osiris.create(model)
}
