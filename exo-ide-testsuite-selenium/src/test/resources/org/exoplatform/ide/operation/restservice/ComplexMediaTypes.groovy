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