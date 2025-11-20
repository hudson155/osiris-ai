package osiris.element.parser

import com.vladsch.flexmark.ast.HardLineBreak
import osiris.element.element.Element
import osiris.element.element.LineBreakElement

internal object HardLineBreakMarkdownParser : MarkdownParser<HardLineBreak>() {
  override fun parse(node: HardLineBreak): List<Element> {
    val element = LineBreakElement
    return listOf(element)
  }
}
