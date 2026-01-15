package osiris.element.element

import com.fasterxml.jackson.annotation.JsonIgnore

public data class CodeBlockElement(
  val language: String?,
  val elements: List<Element>,
) : Element(), Element.WithChildLists {
  @JsonIgnore
  override val childLists: List<List<Element>> =
    listOf(elements)

  override fun withChildLists(childLists: List<List<Element>>): CodeBlockElement {
    check(childLists.size == 1)
    return copy(elements = childLists[0])
  }

  public companion object
}
