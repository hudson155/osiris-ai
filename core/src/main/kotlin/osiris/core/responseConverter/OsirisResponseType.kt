package osiris.core.responseConverter

import dev.langchain4j.model.chat.response.ChatResponse

public abstract class OsirisResponseType<out Response : Any> {
  public abstract fun convert(langchainResponse: ChatResponse): Response
}
