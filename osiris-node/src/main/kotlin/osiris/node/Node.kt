package osiris.node

public abstract class Node(
  public val name: String,
) {
  public abstract suspend fun run(context: Context)
}
