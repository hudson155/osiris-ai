package osiris.core

import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import osiris.testing.OsirisEval
import osiris.testing.OsirisTest
import osiris.testing.OsirisTestMessage

internal class SimpleMathOsirisTest : OsirisTest<String>() {
  override val targetModels: List<ChatModel> =
    listOf(OsirisTestModel.gemini20Flash, OsirisTestModel.openAiGpt41Mini)

  override val evalModel: ChatModel = OsirisTestModel.openAiO3Mini

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

  override fun buildOsiris(model: ChatModel): Osiris<String> =
    Osiris.create(model)
}
