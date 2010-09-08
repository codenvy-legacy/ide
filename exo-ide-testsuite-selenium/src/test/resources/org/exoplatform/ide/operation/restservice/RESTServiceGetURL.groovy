// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.UriInfo
import javax.ws.rs.core.PathSegment


@Path("/testService")
public class TestService {
  @POST
  @Produces("application/xml;charset=utf-8")
  @Consumes("text/plain")
  @Path("Inner/{param}/node/{paramList: .+}")
  public String postXml(@PathParam("param") String pathParam, @PathParam("paramList") List<PathSegment> paramList,
  @HeaderParam("Test-Header") String testHeader,
  @QueryParam("Test Query Parameter") String testQueryParam, String body) {
    return "PathParam:" + pathParam + "; Test-Header: " + testHeader + "; Test Query Parameter: " + testQueryParam + "; Body:" + body;
  }
  
  
  @POST
  @Produces("application/xml")
  @Consumes("application/xml")
  @Path("Inner/{pathParam}")
  public String postFromXmlToXml(@PathParam("pathParam") String pathParam,
  @HeaderParam("Test-Header") String testHeader,
  @QueryParam("Test Query Parameter") String testQueryParam,
  String body) {
    return "PathParam:" + pathParam + "; Test-Header: " + testHeader + "; Test Query Parameter: " + testQueryParam + "; Body:" + body;
    }  
    
    
    @POST
    @Produces("application/json")
    @Consumes("text/plain;charset=utf-8")
    @Path("Inner/{pathParam}")
    public String postFromTextToXml(@PathParam("pathParam") String pathParam,
    @HeaderParam("Test-Header") String testHeader,
    @QueryParam("Test Query Parameter") String testQueryParam,
    String body) {
      return "PathParam:" + pathParam + "; Test-Header: " + testHeader + "; Test Query Parameter: " + testQueryParam + "; Body:" + body;
    }
    
    
    @GET
    @Produces("text/html")
    @Path("Inner/{pathParam}")
    public String getHtml(@PathParam("pathParam") String pathParam,
    @HeaderParam("Test-Header") String testHeader,
    @QueryParam("Test Query Parameter") String testQueryParam,
    String body) {
      return "PathParam:" + pathParam + "; Test-Header: " + testHeader + "; Test Query Parameter: " + testQueryParam + "; Body:" + body;
      } 
      
    }
