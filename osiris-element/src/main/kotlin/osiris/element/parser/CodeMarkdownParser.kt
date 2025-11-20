package osiris.element.parser

import com.vladsch.flexmark.ast.Code
import osiris.element.element.CodeElement
import osiris.element.element.Element

internal object CodeMarkdownParser : MarkdownParser<Code>() {
  override fun parse(node: Code): List<Element> {
    val element = CodeElement(
      content = node.text.toString(),
    )
    return listOf(element)
  }
}
