// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
import java.lang.Exception;
import java.lang.System;
import java.io.PrintStream;

@Path("/my-service")
public class HelloWorld {
  
  private String s = "";
 
  @GET
  @Path("helloworld/{name}")
  public List<Item> hello(@PathParam("name") String name) {
    Exception e;
    def col = []
    PrintStream stream = System.out;
   
    return "Hello " + name
  }
  
  private Integer getInt(Double d)
  {
    Integer ii;
    
  }

  def printClosureOuter = {name2 -> println "Hello, ${name2}" }

  def hello(name3) {
    "Hello, ${name3}"
    String name4 = "test";
  }
}