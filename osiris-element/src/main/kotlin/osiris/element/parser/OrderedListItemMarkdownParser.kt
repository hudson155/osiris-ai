package osiris.element.parser

import com.vladsch.flexmark.ast.OrderedListItem
import osiris.element.element.Element

internal object OrderedListItemMarkdownParser : MarkdownParser<OrderedListItem>() {
  override fun parse(node: OrderedListItem): List<Element> =
    node.children.flatMap { parse(it) }
}
