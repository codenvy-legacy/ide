// simple groovy script
import javax.ws.rs.DefaultValue
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.QueryParam

@Path ("/testMediaTypes")
public class TestService {
   @POST
   @Consumes("application/json")
   @Produces("text/plain")
   @Path("/InnerPath")
   public String post1(TestJSON body) {
      return "Body: " + body.getValue();
   }
   
   @POST
   @Consumes("text/plain")
   @Produces("text/plain")
   @Path("/InnerPath")
   public String post2(String body) {
      return "Body: " + body;
   }
}
public class TestJSON {
   private String value
   
   
   public String getValue() {
      return value
   }
   
   public void setValue(String value) {
      this.value = value
   }
}