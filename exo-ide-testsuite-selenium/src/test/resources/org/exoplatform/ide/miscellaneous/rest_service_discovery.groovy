// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.GET
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.QueryParam
import javax.ws.rs.DefaultValue

@Path("/aa/testService11")
public class TestService {
  @POST
  @Path("Inner/{pathParam}")
  @Consumes("application/xml")
  public String post(
    @PathParam("pathParam") @DefaultValue("pathParam Default") String pathParam,
    @HeaderParam("Test-Header") @DefaultValue("3") int testHeader,
    @QueryParam("TestQueryParam 1") @DefaultValue("true") boolean testQueryParam,
    @DefaultValue("test body") String body
  ) {
    return "POST PathParam: " + pathParam + "; POST Test-Header: " + testHeader + "; POST TestQueryParam: " + testQueryParam + "; POST Body: " + body;
  }

  @GET
  @Path("Inner/{pathParam}")
  @Consumes("application/xml")
  public String get(
    @PathParam("pathParam") @DefaultValue("pathParam Default") String pathParam,
    @HeaderParam("Test-Header") @DefaultValue("3") int testHeader,
    @QueryParam("TestQueryParam 1") @DefaultValue("true") boolean testQueryParam,
    @DefaultValue("test body") String body
  ) {
    return "GET PathParam: " + pathParam + "; GET Test-Header: " + testHeader + "; GET TestQueryParam: " + testQueryParam + "; GET Body: " + body;
  }
}