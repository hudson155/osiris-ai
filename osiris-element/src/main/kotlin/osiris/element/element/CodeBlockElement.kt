package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("CodeBlock")
public data class CodeBlockElement(
  val language: String?,
  val elements: List<Element>,
) : Element(), Element.WithChildLists {
  @Transient
  override val childLists: List<List<Element>> =
    listOf(elements)

  override fun withChildLists(childLists: List<List<Element>>): CodeBlockElement {
    check(childLists.size == 1)
    return copy(elements = childLists[0])
  }

  public companion object
}
