package osiris.agentic

import osiris.agentic.TrackOrderTool.Input
import osiris.core.Tool

internal object TrackOrderTool : Tool<Input, String>("track_order") {
  internal data class Input(
    val orderId: String,
  )

  override suspend fun execute(input: Input): String =
    when (input.orderId) {
      "ord_0" -> "Not shipped yet."
      "ord_1" -> "In transit."
      else -> "Unknown order."
    }
}
