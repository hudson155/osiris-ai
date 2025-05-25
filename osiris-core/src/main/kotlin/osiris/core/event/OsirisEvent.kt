package osiris.core.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.single

public sealed class OsirisEvent<out Response : Any>

public suspend fun <Response : Any> Flow<OsirisEvent<Response>>.get(): Response {
  filterIsInstance<ExceptionOsirisEvent>().firstOrNull()?.let { throw it.e }
  return filterIsInstance<ResponseOsirisEvent<Response>>().single().content
}
