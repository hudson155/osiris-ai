package osiris.osiris

public object OsirisSchema {
  @Target(AnnotationTarget.CLASS)
  public annotation class Name(val name: String)

  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val type: String)

  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val description: String)
}
