package osiris

import dev.langchain4j.agent.tool.ToolExecutionRequest
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.model.chat.request.json.JsonObjectSchema

public abstract class Tool(public val name: String) {
  context(context: Context)
  public suspend fun getToolSpecification(): ToolSpecification =
    ToolSpecification.builder().apply {
      name(this@Tool.name)
      this@Tool.getDescription()?.let { description(it) }
      parameters(this@Tool.parameters())
      configureToolSpecification()
    }.build()

  context(context: Context)
  protected open suspend fun getDescription(): String? =
    null

  context(context: Context)
  protected abstract suspend fun parameters(): JsonObjectSchema

  context(context: Context)
  protected open suspend fun ToolSpecification.Builder.configureToolSpecification(): Unit =
    Unit

  context(context: Context)
  public abstract suspend fun execute(executionRequest: ToolExecutionRequest): String
}
