package osiris.element.parser

import com.vladsch.flexmark.ast.Paragraph
import osiris.element.element.Element
import osiris.element.element.ParagraphElement

internal object ParagraphMarkdownParser : MarkdownParser<Paragraph>() {
  override fun parse(node: Paragraph): List<Element> {
    val content = node.children.flatMap { parse(it) }
    val element = ParagraphElement(content)
    return listOf(element)
  }
}
