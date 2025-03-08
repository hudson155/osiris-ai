package osiris.osiris

import kotlinx.coroutines.channels.ProducerScope

internal typealias OsirisScope<Response> = ProducerScope<OsirisEvent<Response>>
