package osiris.element.parser

import com.vladsch.flexmark.ast.MailLink
import osiris.element.element.Element
import osiris.element.element.MailtoLinkElement

internal object MailLinkMarkdownParser : MarkdownParser<MailLink>() {
  override fun parse(node: MailLink): List<Element> {
    val element = MailtoLinkElement(
      content = node.text.toString(),
      href = node.text.toString(),
    )
    return listOf(element)
  }
}
