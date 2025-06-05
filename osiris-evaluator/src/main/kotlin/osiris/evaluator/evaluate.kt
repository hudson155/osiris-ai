package osiris.evaluator

import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.model.chat.ChatModel
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import osiris.core.convert
import osiris.core.get
import osiris.core.llm

public suspend fun evaluate(
  model: ChatModel,
  messages: List<ChatMessage>,
  criteria: String,
) {
  val response = llm(
    model = model,
    messages = buildList {
      addAll(messages)
      add(
        SystemMessage(
          """
            Evaluate whether the user's question was answered well according to the following criteria.

            $criteria
          """.trimIndent(),
        ),
      )
    },
    responseType = Eval::class,
  )
  val eval = response.get().convert<Eval>().shouldNotBeNull()
  withClue(eval.failureReason) {
    eval.matchesCriteria.shouldBeTrue()
  }
}
