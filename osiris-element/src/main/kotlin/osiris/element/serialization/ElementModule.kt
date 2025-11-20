package osiris.element.serialization

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import osiris.element.element.Element
import osiris.element.element.HeadingElement
import osiris.element.element.LineBreakElement
import osiris.element.element.ParagraphElement
import osiris.element.element.SpanElement
import osiris.element.element.TextElement

public fun elementModule(): SerializersModule =
  SerializersModule {
    polymorphic(Element::class) {
      subclass(HeadingElement::class)
      subclass(LineBreakElement::class)
      subclass(ParagraphElement::class)
      subclass(SpanElement::class)
      subclass(TextElement::class)
    }
  }
