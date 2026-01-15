@file:Suppress("InvalidPackageDeclaration")

package dev.langchain4j.model.chat.request.json

/**
 * TODO: Remove this once https://github.com/langchain4j/langchain4j/issues/4420 is live.
 */
public object JsonNullSchema : JsonSchemaElement {
  override fun description(): Nothing? =
    null
}
