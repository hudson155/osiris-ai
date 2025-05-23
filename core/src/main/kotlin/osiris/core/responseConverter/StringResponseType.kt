package osiris.core.responseConverter

import dev.langchain4j.model.chat.response.ChatResponse

public object StringResponseType : OsirisResponseType<String>() {
  public override fun convert(langchainResponse: ChatResponse): String =
    langchainResponse.aiMessage().text()
}
