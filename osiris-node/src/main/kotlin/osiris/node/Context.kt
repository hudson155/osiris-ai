package osiris.node

import io.ktor.util.Attributes

public class Context : Attributes by Attributes(concurrent = true)
