package osiris.agentic

internal val ecommerceInstructionsBuilder: InstructionsBuilder =
  instructionsBuilder(includeDefaultInstructions = true) {
    add {
      """
        # Ecommerce store
        
        The user is a customer at an ecommerce store.
      """.trimIndent()
    }
  }
