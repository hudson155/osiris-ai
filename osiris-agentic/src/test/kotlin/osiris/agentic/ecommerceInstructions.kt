package osiris.agentic

internal val ecommerceInstructions: Instructions =
  instructions(includeDefaultInstructions = true) {
    add(
      """
        # Ecommerce store

        The user is a customer at an ecommerce store.
      """.trimIndent(),
    )
  }
