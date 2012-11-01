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
package org.exoplatform.ide.client.project.create;

import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;

import java.util.Comparator;

/**
 * Comparator for ordering project types.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ProjectTypesComparator.java Oct 31, 2012 12:47:56 PM azatsarynnyy $
 *
 */
final class ProjectTypesComparator implements Comparator<ProjectType>
{
   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(ProjectType type1, ProjectType type2)
   {
      int indexOfProjectType1 = ProjectResolver.getIndexOfProjectType(type1);
      int indexOfProjectType2 = ProjectResolver.getIndexOfProjectType(type2);

      if (indexOfProjectType1 < indexOfProjectType2)
      {
         return -1;
      }
      else if (indexOfProjectType1 > indexOfProjectType2)
      {
         return 1;
      }

      return 0;
   }
}
