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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectProperties;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectUpdater
{

   /**
    * TODO Temporary method.
    *
    * Detected whether project type is deprecated or not.
    *
    * @param project
    * @return <code>true</code> if deprecated
    */
   public static boolean isNeedUpdateProject(ProjectModel project)
   {
      return ProjectResolver.deprecatedTypes.contains(project.getProjectType())
         && project.getPropertyValues(ProjectProperties.TARGET.value()) == null;
   }

   public interface ProjectUpdatedHandler
   {
      void onProjectUpdated();
   }
   
   /**
    * TODO Temporary method.
    *
    * Is used to detect and set targets to deprecated project types (to support them).
    *
    * @param project
    */
   public static void updateProject(ProjectModel project, final ProjectUpdatedHandler itemUpdatedHandler)
   {
      ArrayList<String> targets = ProjectResolver.resolveProjectTarget(project.getProjectType());
      project.getProperties().add(new PropertyImpl(ProjectProperties.TARGET.value(), targets));

      try
      {
         VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<ItemWrapper>()
         {

            @Override
            protected void onSuccess(ItemWrapper result)
            {
               //loadProject();
               //openProject();
               if (itemUpdatedHandler != null)
               {
                  itemUpdatedHandler.onProjectUpdated();
               }
            }

            @Override
            protected void onFailure(Throwable e)
            {
               IDE.fireEvent(new ExceptionThrownEvent(e));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }   
   
}
