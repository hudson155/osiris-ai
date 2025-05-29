package osiris.schema

import dev.langchain4j.model.chat.request.json.JsonObjectSchema
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.math.BigDecimal
import java.math.BigInteger
import org.junit.jupiter.api.Test

internal class OsirisSchemaTest {
  internal object NonDataObject

  internal data object DataObject

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
    @OsirisSchema.Type("boolean")
    val myBoolean: Byte,
    @OsirisSchema.Type("integer")
    val myInteger: Byte,
    @OsirisSchema.Type("number")
    val myNumber: Byte,
    @OsirisSchema.Type("string")
    val myString: Byte,
  )

  internal data class DataClassTypeOverridden(
    @OsirisSchema.Type("string")
    val myParam: Int,
  )

  internal data class DataClassUnsupportedOverriddenType(
    @OsirisSchema.Type("byte")
    val myParam: Int,
  )

  internal data class DataClassWithDescriptions(
    @OsirisSchema.Description("My boolean.")
    val myBoolean: Boolean,
    @OsirisSchema.Description("My int.")
    val myInt: Int,
    @OsirisSchema.Description("My double.")
    val myDouble: Double,
    @OsirisSchema.Description("My string.")
    val myString: String,
  )

  internal data class DataClassOptionalParam(
    val myBoolean: Boolean = false,
  )

  @Test
  fun `non-data object`() {
    shouldThrow<IllegalArgumentException> {
      osirisSchema(NonDataObject::class)
    }.shouldHaveMessage(
      "Osiris schema osiris.schema.OsirisSchemaTest.NonDataObject must be a data class or data object.",
    )
  }

  @Test
  fun `data object`() {
    osirisSchema(DataObject::class).shouldBe(
      JsonObjectSchema.builder()
        .build(),
    )
  }

  @Test
  fun `non-data class`() {
    shouldThrow<IllegalArgumentException> {
      osirisSchema(NonDataClass::class)
    }.shouldHaveMessage(
      "Osiris schema osiris.schema.OsirisSchemaTest.NonDataClass must be a data class or data object.",
    )
  }

  @Test
  fun `data class, default`() {
    osirisSchema(DataClassDefault::class).shouldBe(
      JsonObjectSchema.builder()
        .addBooleanProperty("myBoolean")
        .addIntegerProperty("myBigInteger")
        .addIntegerProperty("myInt")
        .addIntegerProperty("myLong")
        .addIntegerProperty("myShort")
        .addNumberProperty("myBigDecimal")
        .addNumberProperty("myDouble")
        .addNumberProperty("myFloat")
        .addStringProperty("myString")
        .required(
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
        .build(),
    )
  }

  @Test
  fun `data class, unsupported type`() {
    shouldThrow<IllegalArgumentException> {
      osirisSchema(DataClassUnsupportedType::class)
    }.shouldHaveMessage(
      "Osiris schema for osiris.schema.OsirisSchemaTest.DataClassUnsupportedType::myByte" +
        " is missing @Type," +
        " and the type could not be inferred.",
    )
  }

  @Test
  fun `data class, type specified`() {
    osirisSchema(DataClassTypeSpecified::class).shouldBe(
      JsonObjectSchema.builder()
        .addBooleanProperty("myBoolean")
        .addIntegerProperty("myInteger")
        .addNumberProperty("myNumber")
        .addStringProperty("myString")
        .required(
          "myBoolean",
          "myInteger",
          "myNumber",
          "myString",
        )
        .build(),
    )
  }

  @Test
  fun `data class, type overridden`() {
    osirisSchema(DataClassTypeOverridden::class).shouldBe(
      JsonObjectSchema.builder()
        .addStringProperty("myParam")
        .required("myParam")
        .build(),
    )
  }

  @Test
  fun `data class, unsupported overridden type`() {
    shouldThrow<IllegalArgumentException> {
      osirisSchema(DataClassUnsupportedOverriddenType::class)
    }.shouldHaveMessage(
      "Osiris schema for osiris.schema.OsirisSchemaTest.DataClassUnsupportedOverriddenType::myParam" +
        " specified an unsupported type: byte."
    )
  }

  @Test
  fun `data class, with descriptions`() {
    osirisSchema(DataClassWithDescriptions::class).shouldBe(
      JsonObjectSchema.builder()
        .addBooleanProperty("myBoolean", "My boolean.")
        .addIntegerProperty("myInt", "My int.")
        .addNumberProperty("myDouble", "My double.")
        .addStringProperty("myString", "My string.")
        .required(
          "myBoolean",
          "myInt",
          "myDouble",
          "myString",
        )
        .build(),
    )
  }

  @Test
  fun `data class, optional param`() {
    shouldThrow<IllegalArgumentException> {
      osirisSchema(DataClassOptionalParam::class)
    }.shouldHaveMessage(
      "Osiris schema for osiris.schema.OsirisSchemaTest.DataClassOptionalParam::myBoolean" +
        " must not be optional.",
    )
  }
}
