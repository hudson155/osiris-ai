package osiris.element.parser

import com.vladsch.flexmark.ast.AutoLink
import osiris.element.element.Element
import osiris.element.element.ExternalLinkElement

internal object AutoLinkMarkdownParser : MarkdownParser<AutoLink>() {
  override fun parse(node: AutoLink): List<Element> {
    val element = ExternalLinkElement(
      content = node.text.toString(),
      href = node.url.toString(),
    )
    return listOf(element)
  }
}
