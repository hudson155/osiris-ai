package osiris.element.parser

import com.vladsch.flexmark.ast.ThematicBreak
import osiris.element.element.Element
import osiris.element.element.ThematicBreakElement

internal object ThematicBreakMarkdownParser : MarkdownParser<ThematicBreak>() {
  override fun parse(node: ThematicBreak): List<Element> {
    val element = ThematicBreakElement
    return listOf(element)
  }
}
