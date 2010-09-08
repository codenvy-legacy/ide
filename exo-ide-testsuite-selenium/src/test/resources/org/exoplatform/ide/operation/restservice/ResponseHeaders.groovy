import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path("/test")
public class HelloWorld {
  @GET
  @Path("testgroovy/{name}")
  public String hello(@PathParam("name") String name) {
    return "Hello " + name
  }
}