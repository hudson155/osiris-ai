package osiris.element.element

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("Paragraph")
public data class ParagraphElement(
  val elements: List<Element>,
) : Element(), Element.WithChildLists {
  @Transient
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
