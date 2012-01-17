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
package org.exoplatform.ide.editor.java.client.codeassistant.services;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.JavaClass;
import org.exoplatform.ide.editor.java.client.model.Types;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

/**
 * This service for auto-complete feature. Service need for retrieve information about Groovy classes. <br>
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 2:44:13 PM evgen $
 * 
 */
public abstract class CodeAssistantService
{

   protected String GET_CLASS_URL;

   protected String FIND_CLASS_BY_PREFIX;

   protected String FIND_TYPE;

   protected Loader loader;

   protected String restServiceContext;

   protected CodeAssistantService(String restServiceContext, Loader loader, String getClassUrl,
      String findClassByPrefix, String findType)
   {
      this.loader = loader;
      this.restServiceContext = restServiceContext;

      this.GET_CLASS_URL = getClassUrl;
      this.FIND_CLASS_BY_PREFIX = findClassByPrefix;
      this.FIND_TYPE = findType;
   }

   /**
    * Get Class description (methods, fields etc.) by class FQN
    * 
    * @param fqn
    * @param fileHref for who autocompletion called (Need for find classpath)
    * @param callback - the callback which client has to implement
    */
   public void getClassDescription(String fqn, String projectId, AsyncRequestCallback<TypeInfo> callback)
   {
      String url =
         restServiceContext + GET_CLASS_URL + fqn + "&projectid=" + projectId + "&vfsid="
            + VirtualFileSystem.getInstance().getInfo().getId();
      int status[] = {HTTPStatus.NO_CONTENT, HTTPStatus.OK};
      callback.setSuccessCodes(status);
      try
      {
         AsyncRequest.build(RequestBuilder.GET, url).send(callback);
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Find classes by prefix
    * 
    * @param prefix the first letters of class name
    * @param projectId for who autocompletion called (Need for find classpath)
    * @param callback - the callback which client has to implement
    */
   public void findClassesByPrefix(String prefix, String projectId, AsyncRequestCallback<TypesList> callback)
   {
      String url =
         restServiceContext + FIND_CLASS_BY_PREFIX + prefix + "?where=className" + "&projectid=" + projectId
            + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId();
      try
      {
         AsyncRequest.build(RequestBuilder.GET, url).send(callback);
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Find all classes or annotations or interfaces
    * 
    * @param type class type
    * @param prefix the prefix with type name starts (can be null)
    * @param callback - the callback which client has to implement
    */
   public void findType(Types type, String prefix, String projectId, AsyncRequestCallback<TypesList> callback)
   {
      String url = restServiceContext + FIND_TYPE + type.toString();
      url += "?projectid=" + projectId + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId();
      if (prefix != null && !prefix.isEmpty())
      {
         url += "&prefix=" + prefix;
      }
      try
      {
         AsyncRequest.build(RequestBuilder.GET, url).send(callback);
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

}
