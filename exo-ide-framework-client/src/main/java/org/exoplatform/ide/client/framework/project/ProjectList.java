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
package org.exoplatform.ide.client.framework.project;

import java.util.ArrayList;
import java.util.List;

/**
 * The list of projects.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class ProjectList
{
   /**
    * Project list.
    */
   private List<Project> projects;

   /**
    * @return the projects projects
    */
   public List<Project> getProjects()
   {
      if (projects == null)
      {
         projects = new ArrayList<Project>();
      }
      return projects;
   }

   /**
    * @param projects the projects to set
    */
   public void setProjects(List<Project> projects)
   {
      this.projects = projects;
   }
}
