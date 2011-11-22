/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.core;

import org.exoplatform.ide.core.project.ClasspathProject;
import org.exoplatform.ide.core.project.CreateProject;
import org.exoplatform.ide.core.project.CreateProjectTemplate;
import org.exoplatform.ide.core.project.OpenProject;
import org.exoplatform.ide.core.project.ProjectExplorer;
import org.openqa.selenium.support.PageFactory;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Project extends AbstractTestModule
{
   public ProjectExplorer EXPLORER;

   public CreateProject CREATE;
   
   public OpenProject OPEN;
   
   public ClasspathProject CLASSPATH = new ClasspathProject();
   
   public CreateProjectTemplate TEMPLATE = new CreateProjectTemplate();

   /**
    * 
    */
   public Project()
   {
      EXPLORER = PageFactory.initElements(driver(), ProjectExplorer.class);
      CREATE = PageFactory.initElements(driver(), CreateProject.class);
      OPEN = PageFactory.initElements(driver(), OpenProject.class);
   }
}
