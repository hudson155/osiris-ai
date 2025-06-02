package osiris.core

import dev.langchain4j.agent.tool.ToolSpecification
import kairo.reflect.KairoType
import kairo.serialization.util.kairoWriteSpecial
import kairo.serialization.util.readValueSpecial
import osiris.schema.osirisSchema

public abstract class OsirisTool<in Input : Any, out Output : Any>(
  public val name: String,
) {
  private val inputType: KairoType<Input> = KairoType.from(OsirisTool::class, 0, this::class)
  private val outputType: KairoType<Output> = KairoType.from(OsirisTool::class, 1, this::class)

  public open val description: String? = null

  internal val toolSpecification: ToolSpecification =
    ToolSpecification.builder().apply {
      name(name)
      if (description != null) description(description)
      parameters(osirisSchema(inputType.kotlinClass))
    }.build()

  internal suspend operator fun invoke(string: String): String {
    val input = checkNotNull(osirisMapper.readValueSpecial(string, inputType))
    val output = invoke(input)
    return osirisMapper.kairoWriteSpecial(output, outputType)
  }

  public abstract suspend operator fun invoke(input: Input): Output
}
