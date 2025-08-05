package osiris.prompt

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class CompileTest {
  @Test
  fun `no variables`() {
    runTest {
      val instructions = Instructions { "The odds of the Oilers winning tonight are 42.8%." }
      instructions.compile().get().shouldBe("The odds of the Oilers winning tonight are 42.8%.")
    }
  }

  @Test
  fun `several variables`() {
    runTest {
      val instructions = Instructions {
        """
          The odds of the Oilers winning tonight are {{oilersOdds}}.
          And their odds of winning the Stanley Cup are {{oilers-odds-of-stanley-cup}}.
          If they win tonight, the odds of them winning the Stanley Cup become {{oilers_odds_if_win_tonight}}.
          
          The odds of the Panthers winning tonight are {{ panthersOdds }}.
          And their odds of winning the Stanley Cup are {{ panthers-odds-of-stanley-cup }}.
          If they lose tonight, the odds of them winning the Stanley Cup become {{ panthers_odds_if_lose_tonight }}.
        """.trimIndent()
      }
      instructions.compile {
        put("oilersOdds", "42.8%")
        put("oilers-odds-of-stanley-cup", "21.8%")
        put("oilers_odds_if_win_tonight", "51.6%")
        put("panthersOdds", "57.2%")
        put("panthers-odds-of-stanley-cup", "78.2%")
        put("panthers_odds_if_lose_tonight", "48.4%")
      }.get().shouldBe(
        """
          The odds of the Oilers winning tonight are 42.8%.
          And their odds of winning the Stanley Cup are 21.8%.
          If they win tonight, the odds of them winning the Stanley Cup become 51.6%.
          
          The odds of the Panthers winning tonight are 57.2%.
          And their odds of winning the Stanley Cup are 78.2%.
          If they lose tonight, the odds of them winning the Stanley Cup become 48.4%.
        """.trimIndent(),
      )
    }
  }

  @Test
  fun `strict = true (default)`() {
    runTest {
      val instructions = Instructions { "The odds of the Oilers winning tonight are {{oilersOdds}}." }
      shouldThrow<IllegalStateException> {
        instructions.compile().get()
      }.shouldHaveMessage("Unrecognized key: oilersOdds.")
    }
  }

  @Test
  fun `strict = false`() {
    runTest {
      val instructions = Instructions { "The odds of the Oilers winning tonight are {{oilersOdds}}." }
      instructions.compile(strict = false).get()
        .shouldBe("The odds of the Oilers winning tonight are {{oilersOdds}}.")
    }
  }
}
