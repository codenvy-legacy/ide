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
package org.exoplatform.ide.client.project.packaging;

import java.util.ArrayList;

import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class ProjectTypes
{

   private static ArrayList<String> projects = new ArrayList<String>();

   static
   {
      projects.add(ProjectResolver.APP_ENGINE_JAVA);
      projects.add(ProjectResolver.SERVLET_JSP);
      projects.add(ProjectResolver.SPRING);
      projects.add(ProjectType.JAVA.value());
      projects.add(ProjectType.JSP.value());
      projects.add(ProjectType.AWS.value());
   }

   public static boolean contains(ProjectModel project)
   {
      if (project == null)
      {
         return false;
      }
      
      return projects.contains(project.getProjectType());
   }

}
