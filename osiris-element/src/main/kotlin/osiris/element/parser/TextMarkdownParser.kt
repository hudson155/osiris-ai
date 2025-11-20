package osiris.element.parser

import com.vladsch.flexmark.ast.Text
import osiris.element.element.Element
import osiris.element.element.TextElement

internal object TextMarkdownParser : MarkdownParser<Text>() {
  override fun parse(node: Text): List<Element> {
    val element = TextElement(
      content = node.chars.toString(),
    )
    return listOf(element)
  }
}
