package osiris.node

import osiris.element.element.Element
import osiris.element.parser.MarkdownParser

/**
 * An Osiris Agent should use this to indicate that it is [Element]-aware.
 * The Agent can then determine how to map its own response to [Element]s.
 */
public interface ProvidesElements {
  /**
   * Reusable helper implementation for Osiris Agents that use Markdown output.
   */
  public object Markdown : ProvidesElements {
    override suspend fun elements(text: String): List<Element> =
      MarkdownParser.parse(text)
  }

  public suspend fun elements(text: String): List<Element>
}
