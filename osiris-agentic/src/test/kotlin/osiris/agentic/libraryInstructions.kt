package osiris.agentic

internal val libraryInstructions: Instructions =
  instructions(includeDefaultInstructions = true) {
    add(
      """
        # Library

        The user is the admin at a public library.
      """.trimIndent(),
    )
  }
