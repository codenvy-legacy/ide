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
package org.exoplatform.ide.extension.java.client;

import javax.ws.rs.QueryParam;


/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaClientService.java Jun 21, 2011 12:33:19 PM vereshchaka $
 *
 */
public abstract class JavaClientService
{
   
   private static JavaClientService instance;
   
   /**
    * @return {@link JavaClientService} java client service
    */
   public static JavaClientService getInstance()
   {
      return instance;
   }
   
   protected JavaClientService()
   {
      instance = this;
   }
   
   /**
    * Creates java project.
    * 
    * @param projectName - the name of new project
    * @param projectType - type ot new project
    * @param workDir - the location of new project
    * @param callback - callback, client has to implement
    */
   public abstract void createProject(String projectName, String projectType, String groupId, String artifactId, String version, String workDir, MavenResponseCallback callback);

   /**
    * Clean project.
    * 
    * @param baseDir - the location of project
    * @param callback - callback, client has to implement
    */
   public abstract void cleanProject(String baseDir, MavenResponseCallback callback);
   
   /**
    * Package project.
    * 
    * @param baseDir - the location of project
    * @param callback - callback, client has to implement
    */
   public abstract void packageProject(String baseDir, MavenResponseCallback callback);

}
