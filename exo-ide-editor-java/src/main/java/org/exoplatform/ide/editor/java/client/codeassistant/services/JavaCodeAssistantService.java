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
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.editor.java.client.model.Types;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

/**
 * Implementation of {@link CodeAssistantService}
 * <br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:44:53 PM evgen $
 *
 */
public class JavaCodeAssistantService extends CodeAssistantService
{

   private static JavaCodeAssistantService instance;

   private static final String FIND_BY_PROJECT = "/ide/code-assistant/java/find-in-package";

   public JavaCodeAssistantService(String restServiceContext, Loader loader)
   {
      super(restServiceContext, loader, "/ide/code-assistant/java/class-description?fqn=", //GET_CLASS_URL
         "/ide/code-assistant/java/find-by-prefix/", //  FIND_CLASS_BY_PREFIX
         "/ide/code-assistant/java/find-by-type/");
      instance = this;
   }

   public static JavaCodeAssistantService get()
   {
      return instance;
   }

   /**
    * Find all classes from project with file.
    *   
    * @param fileRelPath for who autocompletion called (Need for find classpath)
    * @param callback - the callback which client has to implement
    */
   public void findClassesByProject(String fileId, String projectId, AsyncRequestCallback<TypesList> callback)
   {
      if (fileId != null)
      {
         String url = restServiceContext + FIND_BY_PROJECT;
         url +=
            "?fileid=" + fileId + "&projectid=" + projectId + "&vfsid="
               + VirtualFileSystem.getInstance().getInfo().getId();
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

   public void findTypeByPrefix(String prefix, Types type, String projectId,
      AsyncRequestCallback<TypesList> callback)
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
