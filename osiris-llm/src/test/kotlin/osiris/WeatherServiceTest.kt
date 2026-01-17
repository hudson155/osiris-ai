package osiris

import dev.langchain4j.data.message.UserMessage
import kairo.testing.postcondition
import kairo.testing.setup
import kairo.testing.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import osiris.evaluator.evaluate

@ExtendWith(AgentTest::class)
internal class WeatherServiceTest {
  @Test
  fun test(
    context: Context,
    weatherService: WeatherService,
  ): Unit =
    runTest {
      with(context) {
        setup {
          history.append(UserMessage("What's the weather in Edmonton and Calgary?"))
        }
        test {
          weatherService.execute()
        }
        postcondition {
          evaluate("Should report that the weather in Edmonton is -20 degrees Celsius and snowing.")
          evaluate("Should report that the weather in Calgary is +10 degrees Celsius and sunny.")
        }
      }
    }
}
