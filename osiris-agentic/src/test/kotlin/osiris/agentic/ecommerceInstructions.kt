package osiris.agentic

import osiris.prompt.InstructionsBuilder
import osiris.prompt.instructionsBuilder

internal val ecommerceInstructionsBuilder: InstructionsBuilder =
  instructionsBuilder(includeDefaultInstructions = true) {
    add {
      """
        # Ecommerce store
        
        The user is a customer at an ecommerce store.
      """.trimIndent()
    }
  }
