// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path("/test-service")
public class HelloWorld {
  @GET
  @Path("runcommand/{name}/{param}")
  public String hello(@PathParam("name") String name, @PathParam("param") String param) {
    return "Hello " + name + ", " + param;
  }
} 
