package osiris.osiris

import com.fasterxml.jackson.databind.json.JsonMapper
import kairo.serialization.jsonMapper

public val osirisMapper: JsonMapper = jsonMapper().build()
