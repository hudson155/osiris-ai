package osiris.langfuseTracing

internal class BatchBuilder() {
  fun build(): BatchIngestion =
    BatchIngestion(
      batch = emptyList()
    )
}
