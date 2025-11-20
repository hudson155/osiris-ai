package osiris.element.parser

import com.vladsch.flexmark.ast.FencedCodeBlock
import osiris.element.element.Element
import osiris.element.element.CodeBlockElement
import osiris.element.element.TextElement

internal object FencedCodeBlockMarkdownParser : MarkdownParser<FencedCodeBlock>() {
  override fun parse(node: FencedCodeBlock): List<Element> {
    val language = node.info.let { if (it.isNull) null else it.toString() }
    val content = TextElement(
      content = node.contentChars.toString().trim('\r', '\n'),
    )
    val element = CodeBlockElement(
      language = language,
      elements = listOf(content),
    )
    return listOf(element)
  }
}
