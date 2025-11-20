package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("NumberedList")
public data class NumberedListElement(
  val items: List<List<Element>>,
) : Element(), Element.WithChildLists {
  @Transient
  override val childLists: List<List<Element>> =
    items

  override fun withChildLists(childLists: List<List<Element>>): NumberedListElement {
    check(childLists.size == items.size)
    return copy(items = childLists)
  }

  public companion object
}
