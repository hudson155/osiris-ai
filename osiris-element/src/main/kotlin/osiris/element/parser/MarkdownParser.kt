package osiris.element.parser

import com.vladsch.flexmark.ast.AutoLink
import com.vladsch.flexmark.ast.BlockQuote
import com.vladsch.flexmark.ast.BulletList
import com.vladsch.flexmark.ast.BulletListItem
import com.vladsch.flexmark.ast.Code
import com.vladsch.flexmark.ast.Emphasis
import com.vladsch.flexmark.ast.FencedCodeBlock
import com.vladsch.flexmark.ast.HardLineBreak
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.ast.IndentedCodeBlock
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ast.MailLink
import com.vladsch.flexmark.ast.OrderedList
import com.vladsch.flexmark.ast.OrderedListItem
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.ast.SoftLineBreak
import com.vladsch.flexmark.ast.StrongEmphasis
import com.vladsch.flexmark.ast.Text
import com.vladsch.flexmark.ast.TextBase
import com.vladsch.flexmark.ast.ThematicBreak
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
        is AutoLink -> AutoLinkMarkdownParser.parse(node)
        is BlockQuote -> BlockQuoteMarkdownParser.parse(node)
        is BulletList -> BulletListMarkdownParser.parse(node)
        is BulletListItem -> BulletListItemMarkdownParser.parse(node)
        is Code -> CodeMarkdownParser.parse(node)
        is Document -> DocumentMarkdownParser.parse(node)
        is Emphasis -> EmphasisMarkdownParser.parse(node)
        is FencedCodeBlock -> FencedCodeBlockMarkdownParser.parse(node)
        is HardLineBreak -> HardLineBreakMarkdownParser.parse(node)
        is Heading -> HeadingMarkdownParser.parse(node)
        is IndentedCodeBlock -> IndentedCodeBlockMarkdownParser.parse(node)
        is Link -> LinkMarkdownParser.parse(node)
        is MailLink -> MailLinkMarkdownParser.parse(node)
        is OrderedList -> OrderedListMarkdownParser.parse(node)
        is OrderedListItem -> OrderedListItemMarkdownParser.parse(node)
        is Paragraph -> ParagraphMarkdownParser.parse(node)
        is SoftLineBreak -> SoftLineBreakMarkdownParser.parse(node)
        is StrongEmphasis -> StrongEmphasisMarkdownParser.parse(node)
        is Text -> TextMarkdownParser.parse(node)
        is TextBase -> TextBaseMarkdownParser.parse(node)
        is ThematicBreak -> ThematicBreakMarkdownParser.parse(node)
        else -> error("Unsupported node: ${node::class.qualifiedName}.")
      }
  }
}
