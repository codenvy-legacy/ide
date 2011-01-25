// simple groovy script
import Path
import javax.ws.rs.GET
import some.pack.String

@Path("/my-service")
public class HelloWorld {

  @ManyToOne(type = RelationshipType.REFERENCE) @Mandatory @org.chromattic.MappedBy("product") def product
  @Property(name = "quantity") def quantity  

  @Override
  @POST
  @Path("helloworld/{name}")
  public Base64 hello(@PathParam("name") ExoLogger name) {
    Base64 a1;
    String a2;  
    
    name = macpro
    name = 0;
    
    return "Hello " + name
  }
  
  Integer b1;

  int b2;
  
  ResourceBundle.Control classFromJavaUtilPackage;

  java.util.prefs.Base64 c;
}