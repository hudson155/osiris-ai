package osiris.osiris.responseConverter

import dev.langchain4j.model.chat.response.ChatResponse

public abstract class ResponseConverter<out Response : Any> {
  public abstract fun convert(langchainResponse: ChatResponse): Response
}
