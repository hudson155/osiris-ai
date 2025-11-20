package osiris.element.parser

import com.vladsch.flexmark.ast.Heading
import osiris.element.element.Element
import osiris.element.element.HeadingElement

internal object HeadingMarkdownParser : MarkdownParser<Heading>() {
  override fun parse(node: Heading): List<Element> {
    val element =
      HeadingElement(
        level = node.level,
        text = node.text.toString(),
      )
    return listOf(element)
  }
}
