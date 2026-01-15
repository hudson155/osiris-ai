package osiris.node

public abstract class Node(
  public val name: String,
) {
  context(context: Context)
  public abstract suspend fun run()
}
