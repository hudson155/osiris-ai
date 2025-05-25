package osiris.core.schema

public object OsirisSchema {
  /**
   * The schema name is REQUIRED on the class.
   */
  @Target(AnnotationTarget.CLASS)
  public annotation class Name(val name: String)

  /**
   * Each parameter MUST specify a type.
   */
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Type(val type: String)

  /**
   * Each parameter MAY specify a description.
   */
  @Target(AnnotationTarget.VALUE_PARAMETER)
  public annotation class Description(val description: String)
}
