package osiris.osiris.responseConverter

import dev.langchain4j.model.chat.response.ChatResponse

public object StringResponseConverter : ResponseConverter<String>() {
  public override fun convert(langchainResponse: ChatResponse): String =
    langchainResponse.aiMessage().text()
}
