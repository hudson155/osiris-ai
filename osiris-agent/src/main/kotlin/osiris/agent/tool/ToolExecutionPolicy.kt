package osiris.agent.tool

public enum class ToolExecutionPolicy {
    /**
     * Indicates that we always want to allow the execution of the tool to be available and to execute.
     */
    ALWAYS_EXECUTE,

    /**
     * Indicates that the tool should not be called more than once per exchange.
     */
    ONCE_PER_EXCHANGE,

    /**
     * Indicates that the tool should not be called with the same arguments more than once per exchange.
     */
    ONCE_PER_EXCHANGE_AND_ARGS,
}