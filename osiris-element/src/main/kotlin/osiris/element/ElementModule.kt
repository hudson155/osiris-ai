package osiris.element

import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.databind.module.SimpleModule
import osiris.element.element.BlockQuoteElement
import osiris.element.element.BulletedListElement
import osiris.element.element.CodeBlockElement
import osiris.element.element.CodeElement
import osiris.element.element.Element
import osiris.element.element.ExternalLinkElement
import osiris.element.element.HeadingElement
import osiris.element.element.LineBreakElement
import osiris.element.element.MailtoLinkElement
import osiris.element.element.NumberedListElement
import osiris.element.element.ParagraphElement
import osiris.element.element.SpanElement
import osiris.element.element.TextElement
import osiris.element.element.ThematicBreakElement

/**
 * Jackson module for [Element] support.
 * In the future, this will be adjusted to support custom elements.
 */
public class ElementModule : SimpleModule() {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)

    context.registerSubtypes(
      NamedType(BlockQuoteElement::class.java, "BlockQuote"),
      NamedType(BulletedListElement::class.java, "BulletedList"),
      NamedType(CodeBlockElement::class.java, "CodeBlock"),
      NamedType(CodeElement::class.java, "Code"),
      NamedType(ExternalLinkElement::class.java, "ExternalLink"),
      NamedType(HeadingElement::class.java, "Heading"),
      NamedType(LineBreakElement::class.java, "LineBreak"),
      NamedType(MailtoLinkElement::class.java, "MailtoLink"),
      NamedType(NumberedListElement::class.java, "NumberedList"),
      NamedType(ParagraphElement::class.java, "Paragraph"),
      NamedType(SpanElement::class.java, "Span"),
      NamedType(TextElement::class.java, "Text"),
      NamedType(ThematicBreakElement::class.java, "ThematicBreak"),
    )
  }
}
