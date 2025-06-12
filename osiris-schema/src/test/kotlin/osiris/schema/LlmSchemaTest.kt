package osiris.schema

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.langchain4j.model.chat.request.json.JsonAnyOfSchema
import dev.langchain4j.model.chat.request.json.JsonArraySchema
import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import dev.langchain4j.model.chat.request.json.JsonStringSchema
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.math.BigDecimal
import java.math.BigInteger
import kairo.id.KairoId
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import osiris.schema.LlmSchema.LlmSchemaException

@Suppress("CyclomaticComplexMethod", "LongMethod", "NestedScopeFunctions")
internal class LlmSchemaTest {
  internal object NonDataObject

  internal data object DataObject

  @Suppress("unused", "UseDataClass")
  internal class NonDataClass(
    val myString: String,
  )

  internal data class DataClassDefault(
    val myBoolean: Boolean,
    val myBigInteger: BigInteger,
    val myInt: Int,
    val myLong: Long,
    val myShort: Short,
    val myBigDecimal: BigDecimal,
    val myDouble: Double,
    val myFloat: Float,
    val myKairoId: KairoId,
    val myString: String,
  )

  internal data class DataClassNested(
    @LlmSchema.Type("string")
    val myParam: Int,
    @LlmSchema.Description("My string.")
    val myString: String,
    @LlmSchema.Description("My outer.")
    val myOuter: Outer,
  ) {
    internal data class Outer(
      @LlmSchema.Type("string")
      val myParam: Int,
      @LlmSchema.Description("My string.")
      val myString: String,
      @LlmSchema.Description("My inner.")
      val myInner: Inner,
      val myParamList: List<@LlmSchema.Type("string") Int>,
      @LlmSchema.Description("My string list.")
      val myStringList: List<@LlmSchema.Description("Each string.") String>,
      @LlmSchema.Description("My inner list.")
      val myInnerList: List<@LlmSchema.Description("Each inner.") Inner>,
    )

    internal data class Inner(
      @LlmSchema.Type("string")
      val myParam: Int,
      @LlmSchema.Description("My string.")
      val myString: String,
      val myParamList: List<@LlmSchema.Type("string") Int>,
      @LlmSchema.Description("My string list.")
      val myStringList: List<@LlmSchema.Description("Each string.") String>,
    )
  }

  internal data class DataClassPolymorphic(
    val vehicle: Vehicle,
  ) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(
      JsonSubTypes.Type(Vehicle.Car::class, name = "Car"),
      JsonSubTypes.Type(Vehicle.Motorcycle::class, name = "Motorcycle"),
      JsonSubTypes.Type(Vehicle.Bicycle::class, name = "Bicycle"),
    )
    internal sealed class Vehicle {
      abstract val model: String?

      abstract val wheels: Int

      internal data class Car(
        override val model: String,
        val plate: String,
        val capacity: Int,
      ) : Vehicle() {
        override val wheels: Int = 4
      }

      internal data class Motorcycle(
        val plate: String,
      ) : Vehicle() {
        override val model: Nothing? = null

        override val wheels: Int = 2
      }

      internal data object Bicycle : Vehicle() {
        override val model: Nothing? = null

        override val wheels: Int = 2
      }
    }
  }

  internal data class DataClassUnsupportedType(
    val myByte: Byte,
  )

  internal data class DataClassTypeSpecified(
    @LlmSchema.Type("boolean")
    val myBoolean: Byte,
    @LlmSchema.Type("integer")
    val myInteger: Byte,
    @LlmSchema.Type("number")
    val myNumber: Byte,
    @LlmSchema.Type("string")
    val myString: Byte,
  )

  internal data class DataClassTypeOverridden(
    @LlmSchema.Type("string")
    val myParam: Int,
  )

  internal data class DataClassUnsupportedOverriddenType(
    @LlmSchema.Type("byte")
    val myParam: Int,
  )

  internal data class DataClassWithDescriptions(
    @LlmSchema.Description("My boolean.")
    val myBoolean: Boolean,
    @LlmSchema.Description("My int.")
    val myInt: Int,
    @LlmSchema.Description("My double.")
    val myDouble: Double,
    @LlmSchema.Description("My string.")
    val myString: String,
  )

  internal data class DataClassOptionalParam(
    val myBoolean: Boolean = false,
  )

  @Test
  fun `non-data object`(): Unit = runTest {
    shouldThrow<LlmSchemaException> {
      LlmSchema.generate(NonDataObject::class)
    }.shouldHaveMessage(
      "Failed to generate LLM schema for osiris.schema.LlmSchemaTest.NonDataObject." +
        " Must be a data class or data object.",
    )
  }

  @Test
  fun `data object`(): Unit = runTest {
    LlmSchema.generate(DataObject::class).shouldBe(
      JsonObjectSchema.builder()
        .build(),
    )
  }

  @Test
  fun `non-data class`(): Unit = runTest {
    shouldThrow<LlmSchemaException> {
      LlmSchema.generate(NonDataClass::class)
    }.shouldHaveMessage(
      "Failed to generate LLM schema for osiris.schema.LlmSchemaTest.NonDataClass." +
        " Must be a data class or data object.",
    )
  }

  @Test
  fun `data class, default`(): Unit = runTest {
    LlmSchema.generate(DataClassDefault::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addBooleanProperty("myBoolean")
        addIntegerProperty("myBigInteger")
        addIntegerProperty("myInt")
        addIntegerProperty("myLong")
        addIntegerProperty("myShort")
        addNumberProperty("myBigDecimal")
        addNumberProperty("myDouble")
        addNumberProperty("myFloat")
        addStringProperty("myKairoId")
        addStringProperty("myString")
        required(
          "myBoolean",
          "myBigInteger",
          "myInt",
          "myLong",
          "myShort",
          "myBigDecimal",
          "myDouble",
          "myFloat",
          "myKairoId",
          "myString",
        )
      }.build(),
    )
  }

  @Test
  fun `data class, nested`(): Unit = runTest {
    LlmSchema.generate(DataClassNested::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addStringProperty("myParam")
        addStringProperty("myString", "My string.")
        addProperty(
          "myOuter",
          JsonObjectSchema.builder().apply {
            description("My outer.")
            addStringProperty("myParam")
            addStringProperty("myString", "My string.")
            addProperty(
              "myInner",
              JsonObjectSchema.builder().apply {
                description("My inner.")
                addStringProperty("myParam")
                addStringProperty("myString", "My string.")
                addProperty(
                  "myParamList",
                  JsonArraySchema.builder().apply {
                    items(JsonStringSchema.builder().build())
                  }.build(),
                )
                addProperty(
                  "myStringList",
                  JsonArraySchema.builder().apply {
                    description("My string list.")
                    items(
                      JsonStringSchema.builder().apply {
                        description("Each string.")
                      }.build(),
                    )
                  }.build(),
                )
                required(
                  "myParam",
                  "myString",
                  "myParamList",
                  "myStringList",
                )
              }.build(),
            )
            addProperty(
              "myParamList",
              JsonArraySchema.builder().apply {
                items(JsonStringSchema.builder().build())
              }.build(),
            )
            addProperty(
              "myStringList",
              JsonArraySchema.builder().apply {
                description("My string list.")
                items(
                  JsonStringSchema.builder().apply {
                    description("Each string.")
                  }.build(),
                )
              }.build(),
            )
            addProperty(
              "myInnerList",
              JsonArraySchema.builder().apply {
                description("My inner list.")
                items(
                  JsonObjectSchema.builder().apply {
                    description("Each inner.")
                    addStringProperty("myParam")
                    addStringProperty("myString", "My string.")
                    addProperty(
                      "myParamList",
                      JsonArraySchema.builder().apply {
                        items(JsonStringSchema.builder().build())
                      }.build(),
                    )
                    addProperty(
                      "myStringList",
                      JsonArraySchema.builder().apply {
                        description("My string list.")
                        items(
                          JsonStringSchema.builder().apply {
                            description("Each string.")
                          }.build(),
                        )
                      }.build(),
                    )
                    required(
                      "myParam",
                      "myString",
                      "myParamList",
                      "myStringList",
                    )
                  }.build(),
                )
              }.build(),
            )
            required(
              "myParam",
              "myString",
              "myInner",
              "myParamList",
              "myStringList",
              "myInnerList",
            )
          }.build(),
        )
        required(
          "myParam",
          "myString",
          "myOuter",
        )
      }.build(),
    )
  }

  @Test
  fun `data class, polymorphic`(): Unit = runTest {
    LlmSchema.generate(DataClassPolymorphic::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addProperty(
          "vehicle",
          JsonAnyOfSchema.builder().apply {
            anyOf(
              JsonObjectSchema.builder().apply {
                addEnumProperty("type", listOf("Car"))
                addStringProperty("model")
                addStringProperty("plate")
                addIntegerProperty("capacity")
                required(
                  "type",
                  "model",
                  "plate",
                  "capacity",
                )
              }.build(),
              JsonObjectSchema.builder().apply {
                addEnumProperty("type", listOf("Motorcycle"))
                addStringProperty("plate")
                required(
                  "type",
                  "plate",
                )
              }.build(),
              JsonObjectSchema.builder().apply {
                addEnumProperty("type", listOf("Bicycle"))
                required("type")
              }.build(),
            )
          }.build(),
        )
        required("vehicle")
      }.build(),
    )
  }

  @Test
  fun `data class, unsupported type`(): Unit = runTest {
    shouldThrow<LlmSchemaException> {
      LlmSchema.generate(DataClassUnsupportedType::class)
    }.shouldHaveMessage(
      "Failed to generate LLM schema for osiris.schema.LlmSchemaTest.DataClassUnsupportedType." +
        " Missing @Type and the type could not be inferred" +
        " for property myByte.",
    )
  }

  @Test
  fun `data class, type specified`(): Unit = runTest {
    LlmSchema.generate(DataClassTypeSpecified::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addBooleanProperty("myBoolean")
        addIntegerProperty("myInteger")
        addNumberProperty("myNumber")
        addStringProperty("myString")
        required(
          "myBoolean",
          "myInteger",
          "myNumber",
          "myString",
        )
      }.build(),
    )
  }

  @Test
  fun `data class, type overridden`(): Unit = runTest {
    LlmSchema.generate(DataClassTypeOverridden::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addStringProperty("myParam")
        required("myParam")
      }.build(),
    )
  }

  @Test
  fun `data class, unsupported overridden type`(): Unit = runTest {
    shouldThrow<LlmSchemaException> {
      LlmSchema.generate(DataClassUnsupportedOverriddenType::class)
    }.shouldHaveMessage(
      "Failed to generate LLM schema for osiris.schema.LlmSchemaTest.DataClassUnsupportedOverriddenType." +
        " Specified unsupported type byte for property myParam.",
    )
  }

  @Test
  fun `data class, with descriptions`(): Unit = runTest {
    LlmSchema.generate(DataClassWithDescriptions::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addBooleanProperty("myBoolean", "My boolean.")
        addIntegerProperty("myInt", "My int.")
        addNumberProperty("myDouble", "My double.")
        addStringProperty("myString", "My string.")
        required(
          "myBoolean",
          "myInt",
          "myDouble",
          "myString",
        )
      }.build(),
    )
  }

  @Test
  fun `data class, optional param`(): Unit = runTest {
    shouldThrow<LlmSchemaException> {
      LlmSchema.generate(DataClassOptionalParam::class)
    }.shouldHaveMessage(
      "Failed to generate LLM schema for osiris.schema.LlmSchemaTest.DataClassOptionalParam." +
        " myBoolean must not be optional.",
    )
  }
}
