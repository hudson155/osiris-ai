package osiris.element.parser

import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.element.element.ParagraphElement
import osiris.element.element.TextElement
import osiris.element.parser.MarkdownParser

internal class ParagraphElementParserTest {
  @Test
  fun `single paragraph`() {
    runTest {
      val string =
        """
        I see your message, but it doesn't seem related to Highbeam,
        your business, or the financial and consumer brand industries.
        How can I assist you with those topics?
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("I see your message, but it doesn't seem related to Highbeam,"),
            TextElement(" "),
            TextElement("your business, or the financial and consumer brand industries."),
            TextElement(" "),
            TextElement("How can I assist you with those topics?"),
          ),
        ),
      )
    }
  }

  @Test
  fun `multiple paragraphs`() {
    runTest {
      val string =
        """
          Focus on Cash Flow:
        As an ecommerce founder, managing cash flow is critical.
        Keep a close eye on your cash and ensure you're balancing your inventory investment
        with the necessary funds to cover marketing, staffing, and operational costs.
        Leverage tools like forecasting and financial modeling
        to predict short-term cash needs and avoid unexpected cash shortages.

        Optimize for Retention:
         While acquiring new customers is important, retaining them is crucial for growth
          Create an experience that turns first-time buyers into repeat customers.
         This includes offering exceptional customer service and loyalty programs.
        The lifetime value of your customers should always be a priority.

        Leverage Data for Growth:
         Data is your best friend when scaling an ecommerce business.
          Dive into your analytics to understand sales trends and marketing performance.
           Use this information to refine your strategies and make data-driven decisions.
            Whether it's optimizing your ad spend, adjusting your product offerings,
             or improving your website experience.
        """.trimIndent()
      val elements = MarkdownParser.parse(string)
      elements.shouldContainExactly(
        ParagraphElement(
          listOf(
            TextElement("Focus on Cash Flow:"),
            TextElement(" "),
            TextElement("As an ecommerce founder, managing cash flow is critical."),
            TextElement(" "),
            TextElement("Keep a close eye on your cash and ensure you're balancing your inventory investment"),
            TextElement(" "),
            TextElement("with the necessary funds to cover marketing, staffing, and operational costs."),
            TextElement(" "),
            TextElement("Leverage tools like forecasting and financial modeling"),
            TextElement(" "),
            TextElement("to predict short-term cash needs and avoid unexpected cash shortages."),
          ),
        ),
        ParagraphElement(
          listOf(
            TextElement("Optimize for Retention:"),
            TextElement(" "),
            TextElement("While acquiring new customers is important, retaining them is crucial for growth"),
            TextElement(" "),
            TextElement("Create an experience that turns first-time buyers into repeat customers."),
            TextElement(" "),
            TextElement("This includes offering exceptional customer service and loyalty programs."),
            TextElement(" "),
            TextElement("The lifetime value of your customers should always be a priority."),
          ),
        ),
        ParagraphElement(
          listOf(
            TextElement("Leverage Data for Growth:"),
            TextElement(" "),
            TextElement("Data is your best friend when scaling an ecommerce business."),
            TextElement(" "),
            TextElement("Dive into your analytics to understand sales trends and marketing performance."),
            TextElement(" "),
            TextElement("Use this information to refine your strategies and make data-driven decisions."),
            TextElement(" "),
            TextElement("Whether it's optimizing your ad spend, adjusting your product offerings,"),
            TextElement(" "),
            TextElement("or improving your website experience."),
          ),
        ),
      )
    }
  }
}
