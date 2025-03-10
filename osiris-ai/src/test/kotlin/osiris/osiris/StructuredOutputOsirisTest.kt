package osiris.osiris

import com.openai.core.JsonArray
import com.openai.core.JsonBoolean
import com.openai.core.JsonObject
import com.openai.core.JsonString
import com.openai.core.Timeout
import com.openai.models.ChatCompletion
import com.openai.models.ChatCompletion.Choice.FinishReason
import com.openai.models.ChatCompletionCreateParams
import com.openai.models.ChatCompletionCreateParams.ResponseFormat
import com.openai.models.ChatCompletionMessage
import com.openai.models.ChatCompletionMessageParam
import com.openai.models.ChatCompletionUserMessageParam
import com.openai.models.ChatModel
import com.openai.models.ResponseFormatJsonSchema
import com.openai.models.ResponseFormatJsonSchema.JsonSchema
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kairo.reflect.kairoType
import kairo.testing.postcondition
import kairo.testing.setup
import kairo.testing.test
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@Suppress("LongMethod", "NestedScopeFunctions")
internal class StructuredOutputOsirisTest : OsirisTest() {
  @OsirisSchema.Name("person")
  internal data class Person(
    @OsirisSchema.Type("object")
    @OsirisSchema.Description("The person's name.")
    val name: FullName,
    @OsirisSchema.Type("number")
    @OsirisSchema.Description("The person's age.")
    val age: Int,
  ) {
    internal data class FullName(
      @OsirisSchema.Type("first")
      @OsirisSchema.Description("The person's first name.")
      val firstName: String,
      @OsirisSchema.Type("last")
      @OsirisSchema.Description("The person's last name.")
      val lastName: String,
    )
  }

  @Test
  fun test(): Unit = runTest {
    val openAi = setup("Configure mocking") {
      val choices = listOf(
        mockk<ChatCompletion.Choice> {
          every { finishReason() } returns FinishReason.STOP
          every { message() } returns ChatCompletionMessage.builder()
            .refusal(null)
            .content(
              """
                {
                  "name": {
                    "firstName": "Jeff",
                    "lastName": "Hudson"
                  },
                  "age": 29
                }
              """.trimIndent(),
            )
            .build()
        },
      )
      return@setup mockChatCompletions(choices)
    }

    val events = test("Execute Osiris") {
      val osiris = Osiris(openAi)
      val messages = listOf(
        ChatCompletionMessageParam.ofUser(
          ChatCompletionUserMessageParam.builder().content("Tell me about a person").build(),
        ),
      )
      val options = OsirisOptions(
        model = { ChatModel.GPT_4O },
        responseType = OsirisResponseType.Json(kairoType<Person>()),
      )
      return@test osiris.execute(messages, options).toList()
    }

    postcondition("Check events") {
      events.shouldHaveSize(3)
      checkEvent0(events[0])
      checkEvent1(events[1])
      checkEvent2(events[2])
    }
  }

  private fun checkEvent0(event: OsirisEvent<Person>) {
    event.shouldBeInstanceOf<OsirisEvent.ChatCompletionRequest>()
    event.params.shouldBe(
      ChatCompletionCreateParams.builder().apply {
        messages(
          listOf(
            ChatCompletionMessageParam.ofUser(
              ChatCompletionUserMessageParam.builder().content("Tell me about a person").build(),
            ),
          ),
        )
        model(ChatModel.GPT_4O)
        n(1)
        parallelToolCalls(false)
        responseFormat(
          ResponseFormat.ofJsonSchema(
            ResponseFormatJsonSchema.builder().apply {
              jsonSchema(
                JsonSchema.builder().apply {
                  name("person")
                  schema(
                    JsonSchema.Schema.builder().apply {
                      putAdditionalProperty("type", JsonString.of("object"))
                      putAdditionalProperty(
                        "properties",
                        JsonArray.of(
                          listOf(
                            JsonObject.of(
                              mapOf(
                                "name" to JsonObject.of(
                                  mapOf(
                                    "type" to JsonString.of("object"),
                                    "description" to JsonString.of("The person's name."),
                                  ),
                                ),
                              ),
                            ),
                            JsonObject.of(
                              mapOf(
                                "age" to JsonObject.of(
                                  mapOf(
                                    "type" to JsonString.of("number"),
                                    "description" to JsonString.of("The person's age."),
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                      )
                      putAdditionalProperty(
                        "required",
                        JsonArray.of(
                          listOf(
                            JsonString.of("fullName"),
                            JsonString.of("age"),
                          ),
                        ),
                      )
                      putAdditionalProperty("additionalProperties", JsonBoolean.of(false))
                    }.build(),
                  )
                  strict(true)
                }.build(),
              )
            }.build(),
          ),
        )
        serviceTier(ChatCompletionCreateParams.ServiceTier.AUTO)
      }.build(),
    )
    event.options.responseValidation.shouldNotBeNull().shouldBeTrue()
    event.options.timeout.shouldBe(Timeout.default())
  }

  private fun checkEvent1(event: OsirisEvent<Person>) {
    event.shouldBeInstanceOf<OsirisEvent.ChatCompletionResponse>()
    event.chatCompletion.choices().should { choices ->
      choices.shouldHaveSize(1)
      choices[0].should { choice ->
        choice.finishReason().shouldBe(FinishReason.STOP)
        choice.message().should { message ->
          message.refusal().shouldBeEmpty()
          message.content().get().shouldBe(
            """
              {
                "name": {
                  "firstName": "Jeff",
                  "lastName": "Hudson"
                },
                "age": 29
              }
            """.trimIndent(),
          )
        }
      }
    }
  }

  private fun checkEvent2(event: OsirisEvent<Person>) {
    event.shouldBeInstanceOf<OsirisEvent.Result<Person>>()
    event.result.shouldBe(
      Person(
        name = Person.FullName(
          firstName = "Jeff",
          lastName = "Hudson",
        ),
        age = 29,
      ),
    )
  }
}
