/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.java.client.projectmodel;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Link;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.rest.AsyncRequest;
import org.exoplatform.ide.rest.AsyncRequestCallback;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class JavaProject extends Project
{
   
   /**
    * Primary nature for Java-scpecific project
    */
   public static final String PRIMARY_NATURE = "java";
   
   /** Java-scpecific project description */
   private JavaProjectDesctiprion description;

   /**
    * @param eventBus
    */
   protected JavaProject(EventBus eventBus)
   {
      super(eventBus);
      this.description = new JavaProjectDesctiprion(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JavaProjectDesctiprion getDescription()
   {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void refreshTree(Folder root,final AsyncCallback<Folder> callback)
   {
      try
      {
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<Folder> internalCallback =
            new AsyncRequestCallback<Folder>(new JavaModelUnmarshaller(root, (JavaProject)root.getProject()))
            {
               @Override
               protected void onSuccess(Folder refreshedRoot)
               {
                  callback.onSuccess(refreshedRoot);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = vfsInfo.getUrlTemplates().get(Link.REL_TREE).getHref();
         url = URL.decode(url).replace("[id]", root.getId());
         AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }
}
