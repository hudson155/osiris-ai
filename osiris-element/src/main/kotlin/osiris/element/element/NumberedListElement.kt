package osiris.element.element

import com.fasterxml.jackson.annotation.JsonIgnore

public data class NumberedListElement(
  val items: List<List<Element>>,
) : Element(), Element.WithChildLists {
  @JsonIgnore
  override val childLists: List<List<Element>> =
    items

  override fun withChildLists(childLists: List<List<Element>>): NumberedListElement {
    check(childLists.size == items.size)
    return copy(items = childLists)
  }

  public companion object
}
