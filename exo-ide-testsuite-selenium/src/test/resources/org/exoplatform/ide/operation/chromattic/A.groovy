@org.chromattic.api.annotations.PrimaryType(name="file")
public class A {
  public def dynamicTyped
  public def String stringTyped
  public @org.chromattic.api.annotations.Name def String stringTypedChromattic
  public @org.chromattic.api.annotations.Property(name="name") def String stringTypedChromatticExplicitGetter
  public String getStringTypedChromatticExplicitGetter() {
    return stringTypedChromatticExplicitGetter
  }
}