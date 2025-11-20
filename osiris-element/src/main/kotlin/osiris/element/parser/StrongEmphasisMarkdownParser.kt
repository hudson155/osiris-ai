package osiris.element.parser

import com.vladsch.flexmark.ast.StrongEmphasis
import osiris.element.element.Element
import osiris.element.element.SpanElement

internal object StrongEmphasisMarkdownParser : MarkdownParser<StrongEmphasis>() {
  override fun parse(node: StrongEmphasis): List<Element> {
    val content = node.children.flatMap { parse(it) }
    val element = SpanElement(
      elements = content,
      bold = true,
    )
    return listOf(element)
  }
}
