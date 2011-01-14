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
import javax.ws.rs.GET
import javax.ws.rs.Consumes
import javax.ws.rs.PathParam
import javax.ws.rs.HeaderParam
import javax.ws.rs.QueryParam
import javax.ws.rs.DefaultValue

@Path("/aa/testService11")
public class TestService {
  @POST
  @Path("Inner/{pathParam}")
  @Consumes("application/xml")
  public String post(
    @PathParam("pathParam") @DefaultValue("pathParam Default") String pathParam,
    @HeaderParam("Test-Header") @DefaultValue("3") int testHeader,
    @QueryParam("TestQueryParam 1") @DefaultValue("true") boolean testQueryParam,
    @DefaultValue("test body") String body
  ) {
    return "POST PathParam: " + pathParam + "; POST Test-Header: " + testHeader + "; POST TestQueryParam: " + testQueryParam + "; POST Body: " + body;
  }

  @GET
  @Path("Inner/{pathParam}")
  @Consumes("application/xml")
  public String get(
    @PathParam("pathParam") @DefaultValue("pathParam Default") String pathParam,
    @HeaderParam("Test-Header") @DefaultValue("3") int testHeader,
    @QueryParam("TestQueryParam 1") @DefaultValue("true") boolean testQueryParam,
    @DefaultValue("test body") String body
  ) {
    return "GET PathParam: " + pathParam + "; GET Test-Header: " + testHeader + "; GET TestQueryParam: " + testQueryParam + "; GET Body: " + body;
  }
}