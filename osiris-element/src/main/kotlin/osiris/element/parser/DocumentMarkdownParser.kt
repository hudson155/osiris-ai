package osiris.element.parser

import com.vladsch.flexmark.util.ast.Document
import osiris.element.element.Element

internal object DocumentMarkdownParser : MarkdownParser<Document>() {
  override fun parse(node: Document): List<Element> =
    node.children.flatMap { parse(it) }
}
