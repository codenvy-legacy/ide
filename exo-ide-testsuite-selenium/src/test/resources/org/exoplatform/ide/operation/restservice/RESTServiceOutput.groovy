// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.QueryParam

@Path("/overralTest")
public class TestService {
  @POST
  @Produces("application/xml;charset=utf-8")
  @Consumes("text/plain")
  @Path("Inner/node/{paramList: .+}")
  public String post(@PathParam("paramList") String paramList,
    @HeaderParam("Test-Header1") String testHeader,
    @QueryParam("Test Query Parameter 1") String testQueryParam,
    String body) {
    return "Param List 1:" + paramList + "; Test Query Parameter 1: " + testQueryParam + "; Test-Header 1: " + testHeader + "; Body:" + body;
  }
 
  @GET
  @Produces("text/html")
  @Consumes("text/plain; charset=utf-8")  
  @Path("Inner/node/{paramList: .+}")
    public String get(@PathParam("paramList") String paramList,
    @HeaderParam("Test-Header2") String testHeader,
    @QueryParam("Test Query Parameter 2") String testQueryParam) {
    return "Param List 2:" + paramList + "; Test Query Parameter 2: " + testQueryParam + "; Test-Header 2: " + testHeader;
  }
}
