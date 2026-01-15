package osiris.element.element

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class Element {
  public interface WithChildLists {
    public val childLists: List<List<Element>>

    public fun withChildLists(childLists: List<List<Element>>): Element
  }
}
