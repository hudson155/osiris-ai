package osiris.agentic

import osiris.instructions.InstructionsBuilder
import osiris.instructions.instructionsBuilder

internal val ecommerceInstructionsBuilder: InstructionsBuilder =
  instructionsBuilder(includeDefaultInstructions = true) {
    add {
      """
        # Ecommerce store
        
        The user is a customer at an ecommerce store.
      """.trimIndent()
    }
  }
