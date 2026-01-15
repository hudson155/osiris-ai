package osiris.element.element

import com.fasterxml.jackson.annotation.JsonIgnore

public data class BulletedListElement(
  val items: List<List<Element>>,
) : Element(), Element.WithChildLists {
  @JsonIgnore
  override val childLists: List<List<Element>> =
    items

  override fun withChildLists(childLists: List<List<Element>>): BulletedListElement {
    check(childLists.size == items.size)
    return copy(items = childLists)
  }

  public companion object
}
