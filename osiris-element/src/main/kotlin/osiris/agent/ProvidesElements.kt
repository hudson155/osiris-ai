package osiris.agent

import osiris.element.element.Element
import osiris.element.parser.MarkdownParser

public interface ProvidesElements {
  public object Markdown : ProvidesElements {
    override suspend fun elements(text: String): List<Element> =
      MarkdownParser.parse(text)
  }

  public suspend fun elements(text: String): List<Element>
}
