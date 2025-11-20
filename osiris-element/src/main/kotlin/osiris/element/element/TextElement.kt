package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Text")
public data class TextElement(
  val content: String,
) : Element() {
  public companion object
}
