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
package org.exoplatform.ide.extension.maven.client;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

/**
 * Implementation of {@link BuilderClientService} service.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: MavenClientServiceImpl.java Feb 21, 2012 12:44:05 PM azatsarynnyy $
 *
 */
public class BuilderClientServiceImpl extends BuilderClientService
{

   private static final String BASE_URL = "/ide/maven";

   private static final String BUILD = BASE_URL + "/build";

   private static final String CANCEL = BASE_URL + "/cancel";

   private static final String STATUS = BASE_URL + "/status";

   private static final String LOG = BASE_URL + "/log";

   private static final String DOWNLOAD = BASE_URL + "/download";

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param restContext
    * @param loader
    */
   public BuilderClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#build(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void build(String uri, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      final String requesrUrl = restServiceContext + BUILD;

      String params = "gituri=" + uri;
      callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
      AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#cancel(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void cancel(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + CANCEL + "/" + buildid;

      AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#status(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void status(String buildid, AsyncRequestCallback<BuildStatus> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + STATUS + "/" + buildid;
      callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
      AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   public void log(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + LOG + "/" + buildid;

      AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.BuilderClientService#download(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void download(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + DOWNLOAD + "/" + buildid;

      AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
   }
}
