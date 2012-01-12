// simple groovy script
import javax.ws.rs.Path
import POST

@Path("/testService11")
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
  public java.lang.String post2(@PathParam("pathParam") String pathParam,
    @HeaderParam("Test-Header2") java.lang.String testHeader,
    @QueryParam("Test Query Parameter 2") String testQueryParam,
    java.lang.String body) throws Exception {
      return "PathParam 2:" + pathParam + "; Test Query Parameter 2: " + testQueryParam + "; Test-Header2: " + testHeader + "; Body: " + body;
  }
  
  public TestService(String property) {
     this.property = property;     
  }

  @Listener
  protected TestService(String property, HashMap<String,String> map) throws java.lang.Exception
  {
     this.map = map;     
     this.border = border;     
  }
}

interface Dep extends String
{
   private String name;
   private int age;
   
   public void addYear();
   
   public java.lang.String greet(
   @PathParam("pathParam") String begin);
   
   private int address;   
 }