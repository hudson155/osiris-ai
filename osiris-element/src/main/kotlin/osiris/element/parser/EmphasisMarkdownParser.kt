package osiris.element.parser

import com.vladsch.flexmark.ast.Emphasis
import osiris.element.element.Element
import osiris.element.element.SpanElement

internal object EmphasisMarkdownParser : MarkdownParser<Emphasis>() {
  override fun parse(node: Emphasis): List<Element> {
    val content = node.children.flatMap { parse(it) }
    val element = SpanElement(
      elements = content,
      italic = true,
    )
    return listOf(element)
  }
}
