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
package org.exoplatform.ide.client.model.template;

import org.exoplatform.ide.client.Images;

import java.util.List;

/**
 * Template for projects.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 21, 2010 $
 *
 */
public class ProjectTemplate extends FolderTemplate
{
   /**
    * The location of class path file.
    */
   private String classPathLocation;

   /**
    * 
    */
   public ProjectTemplate(String name)
   {
      super(name);
   }
   
   public ProjectTemplate(String name, String description, String nodeName, List<Template> templates)
   {
      super(name, description, nodeName, templates);
   }
   
   public ProjectTemplate(String name, String description, boolean isDefault)
   {
      super(name, description, isDefault);
   }
   
   /**
    * @see org.exoplatform.ide.client.model.template.Template#getIcon()
    */
   @Override
   public String getIcon()
   {
      return Images.FileTypes.FOLDER;
   }

   /**
    * @return the classPathLocation
    */
   public String getClassPathLocation()
   {
      return classPathLocation;
   }

   /**
    * @param classPathLocation the classPathLocation to set
    */
   public void setClassPathLocation(String classPathLocation)
   {
      this.classPathLocation = classPathLocation;
   }
}
