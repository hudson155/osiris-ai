package osiris.element.parser

import com.vladsch.flexmark.ast.TextBase
import osiris.element.element.Element

internal object TextBaseMarkdownParser : MarkdownParser<TextBase>() {
  override fun parse(node: TextBase): List<Element> =
    node.children.flatMap { parse(it) }
}
