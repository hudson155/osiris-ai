package osiris.element.parser

import com.vladsch.flexmark.ast.BulletListItem
import osiris.element.element.Element

internal object BulletListItemMarkdownParser : MarkdownParser<BulletListItem>() {
  override fun parse(node: BulletListItem): List<Element> =
    node.children.flatMap { parse(it) }
}
