package osiris.element.parser

import com.vladsch.flexmark.ast.OrderedList
import osiris.element.element.Element
import osiris.element.element.NumberedListElement

internal object OrderedListMarkdownParser : MarkdownParser<OrderedList>() {
  override fun parse(node: OrderedList): List<Element> {
    val items = node.children.map { parse(it) }
    val element = NumberedListElement(items)
    return listOf(element)
  }
}
