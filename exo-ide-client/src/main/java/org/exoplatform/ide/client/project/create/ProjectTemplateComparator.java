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

import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.model.template.ProjectTemplate;

import java.util.Comparator;

/**
 * Comparator for sorting project templates.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectTemplateComparator.java May 16, 2012 16:25:04 AM azatsarynnyy $
 *
 */
final class ProjectTemplateComparator implements Comparator<ProjectTemplate>
{
   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(ProjectTemplate p1, ProjectTemplate p2)
   {
      String projectTemplateType1 = p1.getType();
      String projectTemplateType2 = p2.getType();

      if (projectTemplateType1 == null && projectTemplateType2 == null)
      {
         return 0;
      }

      if (projectTemplateType1 == null)
      {
         return -1;
      }

      if (projectTemplateType2 == null)
      {
         return 1;
      }

      int index1 = ProjectResolver.getIndexOfProjectType(projectTemplateType1);
      int index2 = ProjectResolver.getIndexOfProjectType(projectTemplateType2);

      return index1 - index2;
   }
}
