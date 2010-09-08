//simple groovy script

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam

@Path ("/")
public class HelloWorld{
@Get
@Path ("helloworld/{name}")
public String hello(PathParam("name")String name){
  return "Hello"+name
  }
  }
