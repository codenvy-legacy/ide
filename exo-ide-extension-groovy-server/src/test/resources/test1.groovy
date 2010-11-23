package org.exoplatform.groovy.test

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("test-groovy")
public class Test1 {
  
  
  public Test1() {
  }
  
  @GET
  @Path("/groovy1/{param}/")
  def method(@PathParam("param") String name) {
    return "Hello from groovy to " + name
  }
  
}