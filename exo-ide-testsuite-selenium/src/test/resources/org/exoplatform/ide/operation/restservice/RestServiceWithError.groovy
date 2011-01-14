// simple groovy script
import javax.ws.rs.Path
impo rt javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path("/groovy-test-service")
public class HelloWorld {
  @GET
  @Path("helloworld/{name}")
  public String hello(@PathParam("name") String name) {
    return "Hello " + name;
  }
}
 
