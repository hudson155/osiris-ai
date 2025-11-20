package osiris.element.parser

import com.vladsch.flexmark.ast.BulletList
import osiris.element.element.Element
import osiris.element.element.BulletedListElement

internal object BulletListMarkdownParser : MarkdownParser<BulletList>() {
  override fun parse(node: BulletList): List<Element> {
    val items = node.children.map { parse(it) }
    val element = BulletedListElement(items)
    return listOf(element)
  }
}
