package osiris.agentic

import com.fasterxml.jackson.databind.json.JsonMapper
import kairo.serialization.jsonMapper
import kairo.serialization.property.prettyPrint

internal val traceMapper: JsonMapper =
  jsonMapper {
    prettyPrint = true
  }.build()
