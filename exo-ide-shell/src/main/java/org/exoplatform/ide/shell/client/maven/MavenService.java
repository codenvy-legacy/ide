/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.shell.client.maven;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.shell.client.JobService;

/**
 * Maven build service.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 4, 2012 10:49:32 AM anya $
 * 
 */
public class MavenService
{
   /**
    * Service.
    */
   private static MavenService mavenService;

   /**
    * REST context.
    */
   private final String REST_CONTEXT = "rest/private";

   private static final String BASE_URL = "/ide/maven";

   /**
    * Build project method's path.
    */
   private static final String BUILD = BASE_URL + "/build";

   /**
    * Cancel building project method's path.
    */
   private static final String CANCEL = BASE_URL + "/cancel";

   /**
    * Get status of build method's path.
    */
   private static final String STATUS = BASE_URL + "/status";

   /**
    * Get build log method's path.
    */
   private static final String LOG = BASE_URL + "/log";

   private MavenService()
   {
   }

   /**
    * @return {@link JobService} job service
    */
   public static MavenService getService()
   {
      if (mavenService == null)
      {
         mavenService = new MavenService();
      }
      return mavenService;
   }

   /**
    * Start new build.
    * 
    * @throws RequestException
    */
   public void build(String projectId, String vfsId, AsyncRequestCallback<StringBuilder> callback)
      throws RequestException
   {
      final String requesrUrl = REST_CONTEXT + BUILD;

      String params = "vfsid=" + vfsId + "&projectid=" + projectId;
      callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
      AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * Cancel launched build.
    * 
    * @throws RequestException
    */
   public void cancel(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      final String requestUrl = REST_CONTEXT + CANCEL + "/" + buildid;

      AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * Get status of current build.
    * 
    * @throws RequestException
    */
   public void status(String buildid, AsyncRequestCallback<BuildStatus> callback) throws RequestException
   {
      final String requestUrl = REST_CONTEXT + STATUS + "/" + buildid;
      callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
      AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * Get build log.
    * 
    * @throws RequestException
    */
   public void log(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      final String requestUrl = REST_CONTEXT + LOG + "/" + buildid;

      AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }
}
