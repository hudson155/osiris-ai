package osiris.langfuseTracing

internal data class BatchIngestion(
  val batch: List<IngestionEvent<*>>,
)
