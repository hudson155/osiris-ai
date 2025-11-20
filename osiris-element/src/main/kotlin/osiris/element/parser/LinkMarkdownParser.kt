package osiris.element.parser

import com.vladsch.flexmark.ast.Link
import osiris.element.element.Element
import osiris.element.element.ExternalLinkElement

internal object LinkMarkdownParser : MarkdownParser<Link>() {
  override fun parse(node: Link): List<Element> {
    val element = ExternalLinkElement(
      content = node.text.toString(),
      href = node.url.toString(),
    )
    return listOf(element)
  }
}
