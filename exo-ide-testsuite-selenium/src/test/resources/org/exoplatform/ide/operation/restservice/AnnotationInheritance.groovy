// annotation Inheritance Testing
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam

@Path("/testAnnotationInheritance")
public class TestService implements GetHtml {
   @Path("InnerPath/{pathParam}")
   public String getHtml(@PathParam("pathParam") String pathParam, String body) {
      return "PathParam:" + pathParam ;
   }
}

public interface GetHtml {
   @POST
   @Produces("text/html")
   @Consumes("text/plain")
   String getHtml(@PathParam("pathParam") String pathParam, String body);
}