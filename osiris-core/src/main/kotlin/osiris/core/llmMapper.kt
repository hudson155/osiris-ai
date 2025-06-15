package osiris.core

import com.fasterxml.jackson.databind.json.JsonMapper
import kairo.serialization.jsonMapper

public val llmMapper: JsonMapper = jsonMapper().build()
