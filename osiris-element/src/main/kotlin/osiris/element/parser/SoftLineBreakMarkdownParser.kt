package osiris.element.parser

import com.vladsch.flexmark.ast.SoftLineBreak
import osiris.element.element.Element
import osiris.element.element.TextElement

internal object SoftLineBreakMarkdownParser : MarkdownParser<SoftLineBreak>() {
  override fun parse(node: SoftLineBreak): List<Element> {
    val element = TextElement(" ")
    return listOf(element)
  }
}
