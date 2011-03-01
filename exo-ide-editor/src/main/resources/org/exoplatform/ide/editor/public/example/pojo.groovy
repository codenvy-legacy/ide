import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import org.json.simple.JSONValue

@Path("/mashup")
public class HelloWorld {
 @GET
 @Path("twitter_flickr")
 public String hello(@PathParam("name") String name) {
   def obj = JSONValue.parse("http://api.twitter.com/1/trends/current.json".toURL().getText())
   def trends = obj.get("trends")
   def names = []
   trends.get(trends.keySet().toArray()[0]).each() {it ->
       names.add(it.get("name"))
   }
   return names.toString()
   
 }}