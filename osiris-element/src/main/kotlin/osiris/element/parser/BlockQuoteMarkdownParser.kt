package osiris.element.parser

import com.vladsch.flexmark.ast.BlockQuote
import osiris.element.element.BlockQuoteElement
import osiris.element.element.Element

internal object BlockQuoteMarkdownParser : MarkdownParser<BlockQuote>() {
  override fun parse(node: BlockQuote): List<Element> {
    val content = node.children.flatMap { parse(it) }
    val element = BlockQuoteElement(content)
    return listOf(element)
  }
}
