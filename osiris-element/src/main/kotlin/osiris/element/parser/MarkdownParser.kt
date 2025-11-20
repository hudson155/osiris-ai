package osiris.element.parser

import com.vladsch.flexmark.ast.Emphasis
import com.vladsch.flexmark.ast.HardLineBreak
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.ast.SoftLineBreak
import com.vladsch.flexmark.ast.StrongEmphasis
import com.vladsch.flexmark.ast.Text
import com.vladsch.flexmark.ast.TextBase
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import osiris.element.element.Element

public sealed class MarkdownParser<T : Node> {
  internal abstract fun parse(node: T): List<Element>

  public companion object {
    public fun parse(string: String): List<Element> {
      val options = MutableDataSet().apply {
        set(Parser.EXTENSIONS, listOf(AutolinkExtension.create()))
      }
      val parser = Parser.builder(options).build()
      val document = parser.parse(string)
      return parse(document)
    }

    protected fun parse(node: Node): List<Element> =
      when (node) {
        is Document -> DocumentMarkdownParser.parse(node)
        is Emphasis -> EmphasisMarkdownParser.parse(node)
        is HardLineBreak -> HardLineBreakMarkdownParser.parse(node)
        is Paragraph -> ParagraphMarkdownParser.parse(node)
        is SoftLineBreak -> SoftLineBreakMarkdownParser.parse(node)
        is StrongEmphasis -> StrongEmphasisMarkdownParser.parse(node)
        is Text -> TextMarkdownParser.parse(node)
        is TextBase -> TextBaseMarkdownParser.parse(node)
        else -> error("Unsupported node: ${node::class.qualifiedName}.")
      }
  }
}
