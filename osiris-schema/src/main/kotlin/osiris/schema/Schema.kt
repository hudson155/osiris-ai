package osiris.schema

import kotlinx.serialization.SerialInfo

public object Schema {
  @Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
  @Retention(AnnotationRetention.SOURCE)
  @SerialInfo
  public annotation class Description(val value: String)
}
