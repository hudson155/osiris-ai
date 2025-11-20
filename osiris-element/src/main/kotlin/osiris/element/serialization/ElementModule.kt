package osiris.element.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
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

public fun elementModule(): SerializersModule =
  SerializersModule {
    polymorphic(Element::class) {
      subclass(BlockQuoteElement::class)
      subclass(BulletedListElement::class)
      subclass(CodeBlockElement::class)
      subclass(CodeElement::class)
      subclass(ExternalLinkElement::class)
      subclass(HeadingElement::class)
      subclass(LineBreakElement::class)
      subclass(MailtoLinkElement::class)
      subclass(NumberedListElement::class)
      subclass(ParagraphElement::class)
      subclass(SpanElement::class)
      subclass(TextElement::class)
    }
  }
