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

import org.exoplatform.ide.client.framework.vfs.Folder;

/**
 * Represents information about created project.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class Project extends Folder
{
   /**
    * Physical location of project's class path file.
    */
   private String classPathLocation;
   
   /**
    * The id of saved project.
    */
   private String nodeId;
   
   /**
    * @param href location of created project
    * @param classpathLocation location of class path file in project
    */
   public Project(String href, String classpathLocation)
   {
      super(href);
      this.classPathLocation = classpathLocation;
      this.nodeId = null;
   }

   /**
    * @param href location of created project
    * @param classpathLocation location of class path file in project
    * @param nodeId id of saved node
    */
   public Project(String href, String classpathLocation, String nodeId)
   {
      super(href);
      this.classPathLocation = classpathLocation;
      this.nodeId = nodeId;
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

   /**
    * @return the nodeId
    */
   public String getNodeId()
   {
      return nodeId;
   }

   /**
    * @param nodeId the nodeId to set
    */
   public void setNodeId(String nodeId)
   {
      this.nodeId = nodeId;
   }
}
