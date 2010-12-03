/*
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
package org.exoplatform.ide.netvibes.service;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.CookieModule;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.common.http.client.ParseException;
import org.exoplatform.services.jcr.webdav.WebDavService;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.exoplatform.services.rest.resource.ResourceContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@Path("/ide/netvibes")
public class NetvibesWidgetPreviewService implements ResourceContainer
{

   private WebDavService webDavService;

   private static String NETVIBES_URL = "http://api.eco.netvibes.com";

   private static String SUBMIT = "/submit/?";

   public NetvibesWidgetPreviewService(WebDavService webDavService)
   {
      this.webDavService = webDavService;
   }

   @GET
   @Path("/{repoName}/{repoPath:.*}/")
   public Response showContent(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.RANGE) String rangeHeader,
      @HeaderParam(ExtHttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince, @QueryParam("version") String version,
      @Context UriInfo uriInfo)
   {

      Response response = webDavService.get(repoName, repoPath, rangeHeader, ifModifiedSince, version, uriInfo);

      if (response.getStatus() != HTTPStatus.OK)
      {
         return response;
      }

      return Response.fromResponse(response).type("text/html").build();

   }

   @POST
   @Path("/deploy")
   public String deployNetvibesWidget(String inputStream, @QueryParam("login") String login,
      @QueryParam("password") String password, @QueryParam("secretkey") String secretkey,
      @QueryParam("apikey") String apikey)
   {
      SortedMap<String, String> sign = new TreeMap<String, String>();
      sign.put("apikey", apikey);
      sign.put("content", inputStream);
      sign.put("secret", secretkey);

      List<String> tempSign = new ArrayList<String>();
      for (String key : sign.keySet())
      {
         tempSign.add(key + "=" + sign.get(key));
      }

      String signParametr = StringUtils.join(tempSign, "&");

      try
      {
         MessageDigest md = MessageDigest.getInstance("SHA1");
         signParametr = byteArray2Hex((md.digest(signParametr.getBytes())));
      }
      catch (NoSuchAlgorithmException noSuchAlgorithmException)
      {
         throw new WebApplicationException(noSuchAlgorithmException, createErrorResponse(noSuchAlgorithmException, 500));
      }

      URL url;

      try
      {
         url = new URL(NETVIBES_URL);
         NVPair headers[] =
            new NVPair[]{new NVPair(javax.ws.rs.core.HttpHeaders.CONTENT_TYPE, "application/atom+xml;type=entry")};
         HTTPConnection connection = new HTTPConnection(url);
         connection.removeModule(CookieModule.class);
         connection.setAllowUserInteraction(false);
         connection.addBasicAuthorization(null, login, password);
         HTTPResponse response =
            connection.Post(NETVIBES_URL + SUBMIT + "apikey=" + apikey + "&sign=" + signParametr,
               inputStream.getBytes(), headers);

         return response.getText();
      }
      catch (MalformedURLException malformedURLException)
      {
         throw new WebApplicationException(malformedURLException, createErrorResponse(malformedURLException, 500));
      }
      catch (IOException ioException)
      {
         throw new WebApplicationException(ioException, createErrorResponse(ioException, 500));
      }
      catch (ModuleException moduleException)
      {
         throw new WebApplicationException(moduleException, createErrorResponse(moduleException, 500));
      }
      catch (ParseException parseException)
      {
         throw new WebApplicationException(parseException, createErrorResponse(parseException, 500));
      }
   }

   /**
    * Convert array of bytes to hex.
    * 
    * @param hash
    * @return {@link String}
    */
   private static String byteArray2Hex(byte[] hash)
   {
      Formatter formatter = new Formatter();
      for (byte b : hash)
      {
         formatter.format("%02x", b);
      }
      return formatter.toString();
   }

   /**
    * Create response to send error, when it occurs.
    * 
    * @param t exception
    * @param status http status
    * @return {@link Response} formed response
    */
   protected Response createErrorResponse(Throwable t, int status)
   {
      return Response.status(status).entity(t.getMessage()).type("text/plain").build();
   }
}
