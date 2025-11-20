package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Code")
public data class CodeElement(
  val content: String,
) : Element() {
  public companion object
}
