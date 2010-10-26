// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.QueryParam

@Path("/testMediaTypes")
public class TestService {
  @POST
  @Consumes("application/xml")

  @Produces("text/html")
  @Path("InnerPath/{pathParam}")
  public String post1(@PathParam("pathParam") String pathParam,
    @HeaderParam("Test-Header1") String testHeader,
    @QueryParam("Test Query Parameter 1") String testQueryParam,
    String body) {
      return "PathParam 1:" + pathParam + "; Test Query Parameter 1: " + testQueryParam + "; Test-Header1: " + testHeader + "; Body: " + body;
  }

  @POST
  @Consumes("application/xml")

  @Produces("application/json")
  @Path("InnerPath/{pathParam}")
  public String post2(@PathParam("pathParam") String pathParam,
    @HeaderParam("Test-Header2") String testHeader,
    @QueryParam("Test Query Parameter 2") String testQueryParam,
    String body) {
      return "PathParam 2:" + pathParam + "; Test Query Parameter 2: " + testQueryParam + "; Test-Header2: " + testHeader + "; Body: " + body;
  }

}
