/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
// simple groovy script
import javax.ws.rs.Path
import javax.ws.rs.POST

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
    java.lang.String body) {
      return "PathParam 2:" + pathParam + "; Test Query Parameter 2: " + testQueryParam + "; Test-Header2: " + testHeader + "; Body: " + body;
  }
}

class Dep extends String
{
   private String name;
   private int age;
   
   public int getAge(){
     return age;
   }
   
   public void addYear()
   { int i = 1;
     age += i;
   }
   
   public String greet(
   String begin) {
     return begin+", " + name + "!";  }

   private int address;   
 }