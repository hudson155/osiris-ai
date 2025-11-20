package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Heading")
public data class HeadingElement(
  val level: Int,
  val text: String,
) : Element() {
  public companion object
}
