package osiris.langfuse

import com.fasterxml.jackson.annotation.JsonInclude
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.basicAuth
import io.ktor.http.ContentType
import kairo.protectedString.ProtectedString
import kairo.rest.client.KairoClient
import kairo.rest.client.createKairoClient
import kairo.rest.serialization.JacksonConverter
import kairo.serialization.jsonMapper
import kairo.serialization.property.allowUnknownProperties

/**
 *
 */
@Suppress("UseDataClass")
public class Langfuse(
  private val url: String,
  public val publicKey: String,
  public val secretKey: ProtectedString,
) {
  public val client: KairoClient =
    createKairoClient {
      install(ContentNegotiation) {
        register(
          contentType = ContentType.Application.Json,
          converter = JacksonConverter(
            mapper = jsonMapper {
              allowUnknownProperties = true
            }.build {
              serializationInclusion(JsonInclude.Include.NON_NULL)
            },
          ),
        )
      }
      defaultRequest {
        url(this@Langfuse.url)
        @OptIn(ProtectedString.Access::class)
        basicAuth(publicKey, secretKey.value)
      }
    }
}
