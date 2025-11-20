package osiris.element.parser

import com.vladsch.flexmark.ast.IndentedCodeBlock
import osiris.element.element.Element
import osiris.element.element.CodeBlockElement
import osiris.element.element.TextElement

internal object IndentedCodeBlockMarkdownParser : MarkdownParser<IndentedCodeBlock>() {
  override fun parse(node: IndentedCodeBlock): List<Element> {
    val content = TextElement(
      content = node.contentChars.toString().trim('\r', '\n'),
    )
    val element = CodeBlockElement(
      language = null,
      elements = listOf(content),
    )
    return listOf(element)
  }
}
