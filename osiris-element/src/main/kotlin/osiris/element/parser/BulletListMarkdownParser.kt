package osiris.element.parser

import com.vladsch.flexmark.ast.BulletList
import osiris.element.element.BulletedListElement
import osiris.element.element.Element

internal object BulletListMarkdownParser : MarkdownParser<BulletList>() {
  override fun parse(node: BulletList): List<Element> {
    val items = node.children.map { parse(it) }
    val element = BulletedListElement(items)
    return listOf(element)
  }
}
