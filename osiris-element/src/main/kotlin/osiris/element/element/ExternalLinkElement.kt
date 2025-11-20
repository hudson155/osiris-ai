package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ExternalLink")
public data class ExternalLinkElement(
  val content: String,
  val href: String,
) : Element() {
  public companion object
}
