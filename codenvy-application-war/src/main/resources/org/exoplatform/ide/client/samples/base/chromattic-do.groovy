import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.Property

@PrimaryType(name="nt:unstructured")
class DataObject 
{
  @Property(name = "a") def String a
}