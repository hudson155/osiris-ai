package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.math.BigDecimal
import java.math.BigInteger
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class OsirisSchemaTest {
  internal object NonDataObject

  internal data object DataObject

  @Suppress("UseDataClass")
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
    val myString: String,
  )

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
    shouldThrow<IllegalArgumentException> {
      llmSchema(NonDataObject::class)
    }.shouldHaveMessage(
      "Osiris schema osiris.schema.OsirisSchemaTest.NonDataObject must be a data class or data object.",
    )
  }

  @Test
  fun `data object`(): Unit = runTest {
    llmSchema(DataObject::class).shouldBe(
      JsonObjectSchema.builder()
        .build(),
    )
  }

  @Test
  fun `non-data class`(): Unit = runTest {
    shouldThrow<IllegalArgumentException> {
      llmSchema(NonDataClass::class)
    }.shouldHaveMessage(
      "Osiris schema osiris.schema.OsirisSchemaTest.NonDataClass must be a data class or data object.",
    )
  }

  @Test
  fun `data class, default`(): Unit = runTest {
    llmSchema(DataClassDefault::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addBooleanProperty("myBoolean")
        addIntegerProperty("myBigInteger")
        addIntegerProperty("myInt")
        addIntegerProperty("myLong")
        addIntegerProperty("myShort")
        addNumberProperty("myBigDecimal")
        addNumberProperty("myDouble")
        addNumberProperty("myFloat")
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
          "myString",
        )
      }.build(),
    )
  }

  @Test
  fun `data class, unsupported type`(): Unit = runTest {
    shouldThrow<IllegalArgumentException> {
      llmSchema(DataClassUnsupportedType::class)
    }.shouldHaveMessage(
      "Osiris schema for osiris.schema.OsirisSchemaTest.DataClassUnsupportedType::myByte" +
        " is missing @Type," +
        " and the type could not be inferred.",
    )
  }

  @Test
  fun `data class, type specified`(): Unit = runTest {
    llmSchema(DataClassTypeSpecified::class).shouldBe(
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
    llmSchema(DataClassTypeOverridden::class).shouldBe(
      JsonObjectSchema.builder().apply {
        addStringProperty("myParam")
        required("myParam")
      }.build(),
    )
  }

  @Test
  fun `data class, unsupported overridden type`(): Unit = runTest {
    shouldThrow<IllegalArgumentException> {
      llmSchema(DataClassUnsupportedOverriddenType::class)
    }.shouldHaveMessage(
      "Osiris schema for osiris.schema.OsirisSchemaTest.DataClassUnsupportedOverriddenType::myParam" +
        " specified an unsupported type: byte.",
    )
  }

  @Test
  fun `data class, with descriptions`(): Unit = runTest {
    llmSchema(DataClassWithDescriptions::class).shouldBe(
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
    shouldThrow<IllegalArgumentException> {
      llmSchema(DataClassOptionalParam::class)
    }.shouldHaveMessage(
      "Osiris schema for osiris.schema.OsirisSchemaTest.DataClassOptionalParam::myBoolean" +
        " must not be optional.",
    )
  }
}
