package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MailtoLink")
public data class MailtoLinkElement(
  val content: String,
  val href: String,
) : Element() {
  public companion object
}
