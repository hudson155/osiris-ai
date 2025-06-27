# Osiris Schemas

Osiris supports automatic LLM (OpenAPI) **schema generation** for **structured output** and **tool calls**,
so you don't need to manage schemas yourself.

## Installation

Included by default with both [osiris-chat](../osiris-chat) and [osiris-agentic](../osiris-agentic).

## Annotations

- `@LlmSchema.SchemaName`:
  Some providers (such as OpenAI) require that structured output schemas specify a name.
  This is only necessary for structured output, not for tool calls.
- `@LlmSchema.Type`:
  Support custom types or override the type for a supported primitive.
  The value can be `boolean`, `integer`, `string`, or `number`.
- `@LlmSchema.Description`:
  Descriptions are available to the LLM, helping it understand your schema better.

## Structured output

For structured output, just create a data class and specify the schema name.

```kotlin
@LlmSchema.SchemaName("person") // Required!
data class Person(
  @LlmSchema.Description("Their full name.") // Optional additional context for the LLM.
  val name: String,
  val age: Int,
)
```

Then use it by specifying `responseType = Person::class` in the LLM call.

```kotlin
val messages = listOf(
  UserMessage("Jeff Hudson, 29, is a software engineer. He's also a pilot and an ultra trail runner."),
  SystemMessage("Provide a JSON representation of the person matching this description."),
)
val flow = llm(
  model = modelFactory.openAi("gpt-4.1-nano"),
  messages = messages,
  responseType = Person::class,
)

flow.response().convert<Person>()
// Person(name=Jeff Hudson, age=29)
```

## Tool calls

For tool calls, the `Input` data class doesn't need a schema name.

```kotlin
class WeatherTool : SimpleTool<WeatherTool.Input>("weather") {
  data class Input(
    @LlmSchema.Description("The city to get the weather for.")
    val location: String,
  )

  override val description: LazySupplier<String> =
    LazySupplier { "Gets the weather." }

  override suspend fun execute(input: Input): String =
    TODO("Your implementation.")
}
```

## Type support

**Schemas must be data classes** (or data objects).

### Primitives

The following primitive types are supported out-of-the-box:

- For **boolean**: `Boolean`
- For **integer**: `BigInteger`, `Int`, `Long`, `Short`
- For **number**: `BigDecimal`, `Double`, `Float`
- For **string**: `KairoId`, `String`

### Custom types

To use other types, add the `@LlmSchema.Type` annotation to a property.
The value can be `boolean`, `integer`, `string`, or `number`.

```kotlin
@LlmSchema.SchemaName("person")
data class Person(
  @LlmSchema.Type("string")
  val name: MyType,
  val age: Int,
)
```

You can also use this approach to _override_ the type for a supported primitive.

### Lists

Lists are supported using `List`.

```kotlin
data class Input(
  @LlmSchema.Description("The cities to get the weather for.")
  val locations: List<String>,
)
```

### Nesting

You can also nest data classes.

```kotlin
@LlmSchema.SchemaName("person")
data class Person(
  val name: FullName,
  val age: Int,
) {
  data class FullName(
    val first: String,
    val last: String,
  )
}
```

### Polymorphism

Polymorphism is supported, _but not at the root level_ due to OpenAI constraints.
Use a **sealed class**.

```kotlin
@LlmSchema.Polymorphic(
  discriminator = "type",
  subTypes = [
    LlmSchema.Polymorphic.Subtype(Vehicle.Car::class, name = "Car"),
    LlmSchema.Polymorphic.Subtype(Vehicle.Motorcycle::class, name = "Motorcycle"),
    LlmSchema.Polymorphic.Subtype(Vehicle.Bicycle::class, name = "Bicycle"),
  ],
)
internal sealed class Vehicle {
  abstract val model: String?

  abstract val wheels: Int

  data class Car(
    override val model: String,
    val plate: String,
    val capacity: Int,
  ) : Vehicle() {
    override val wheels: Int = 4
  }

  data class Motorcycle(
    val plate: String,
  ) : Vehicle() {
    override val model: Nothing? = null

    override val wheels: Int = 2
  }

  data object Bicycle : Vehicle() {
    override val model: Nothing? = null

    override val wheels: Int = 2
  }
}
```
