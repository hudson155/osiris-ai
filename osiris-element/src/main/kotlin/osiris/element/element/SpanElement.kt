package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("Span")
public data class SpanElement(
  val elements: List<Element>,
  val bold: Boolean = false,
  val italic: Boolean = false,
) : Element(), Element.WithChildLists {
  @Transient
  override val childLists: List<List<Element>> =
    listOf(elements)

  override fun withChildLists(childLists: List<List<Element>>): SpanElement {
    check(childLists.size == 1)
    return copy(elements = childLists[0])
  }

  public companion object
}
