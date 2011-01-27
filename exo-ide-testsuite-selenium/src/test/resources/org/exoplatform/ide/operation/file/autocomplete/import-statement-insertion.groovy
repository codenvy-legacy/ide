// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import java.util.prefs.Base64

@Path("/my-service")
public class HelloWorld {
  Base64 test;
  
  @GET
  @Path("helloworld/{name}")
  public String hello(@PathParam("name") String name) {



     
    return "Hello " + name
  }
}