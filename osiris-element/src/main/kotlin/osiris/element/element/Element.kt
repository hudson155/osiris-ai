package osiris.element.element

import kotlinx.serialization.Serializable

@Serializable
public abstract class Element {
  public interface WithChildLists {
    public val childLists: List<List<Element>>

    public fun withChildLists(childLists: List<List<Element>>): Element
  }
}
