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

import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Comparator;

/**
 * Comparator for ordering projects by name.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectComparator.java Apr 25, 2012 11:45:04 AM azatsarynnyy $
 *
 */
final class ProjectComparator implements Comparator<ProjectModel>
{
   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(ProjectModel p1, ProjectModel p2)
   {
      String projectName1 = p1.getName();
      String projectName2 = p2.getName();

      if (projectName1 == null && projectName2 == null)
      {
         return 0;
      }

      if (projectName1 == null)
      {
         return 1;
      }

      if (projectName2 == null)
      {
         return -1;
      }

      return projectName1.compareTo(projectName2);
   }
}
