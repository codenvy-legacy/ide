/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.eclipse.jdt.client.refactoring;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Implementation of {@link RefactoringClientService} service.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringClientServiceImpl.java Jan 17, 2013 4:16:29 PM azatsarynnyy $
 *
 */
public class RefactoringClientServiceImpl extends RefactoringClientService
{

   /**
    * Base url.
    */
   private static final String BASE_URL = "/ide/java/refactoring";

   /**
    * Build project method's path.
    */
   private static final String RENAME = BASE_URL + "/rename";

   /**
    * REST-service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param restContext REST-service context
    * @param loader loader to show on server request
    */
   public RefactoringClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringClientService#rename(java.lang.String, java.lang.String, java.lang.String,
    *       int, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    * 
    * @throws RequestException
    */
   @Override
   public void rename(String vfsId, String projectId, String fqn, int offset, String newName,
      AsyncRequestCallback<Object> callback) throws RequestException
   {
      final String requesrUrl = restServiceContext + RENAME;

      String params =
         "vfsid=" + vfsId + "&projectid=" + projectId + "&fqn=" + fqn + "&offset=" + offset + "&newName=" + newName;
      AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).loader(loader).send(callback);
   }

}
