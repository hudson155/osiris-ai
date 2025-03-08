package osiris.osiris

import com.openai.models.ChatCompletionCreateParams.ResponseFormat
import com.openai.models.ResponseFormatText

public abstract class OsirisResponseType<out Response : Any> {
  internal abstract fun responseFormat(): ResponseFormat

  internal abstract fun convert(string: String): Response

  public class Text : OsirisResponseType<String>() {
    override fun responseFormat(): ResponseFormat =
      ResponseFormat.ofText(ResponseFormatText.builder().build())

    override fun convert(string: String): String =
      string
  }
}
