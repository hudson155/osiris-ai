package osiris.element.element

import com.fasterxml.jackson.annotation.JsonIgnore

public data class ParagraphElement(
  val elements: List<Element>,
) : Element(), Element.WithChildLists {
  @JsonIgnore
  override val childLists: List<List<Element>> =
    listOf(elements)

  override fun withChildLists(childLists: List<List<Element>>): ParagraphElement {
    check(childLists.size == 1)
    return copy(elements = childLists[0])
  }

  public companion object {
    public fun text(text: String): ParagraphElement =
      ParagraphElement(listOf(TextElement(text)))
  }
}
