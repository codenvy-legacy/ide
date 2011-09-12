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
package org.exoplatform.ide.extension.samples.client;

import org.exoplatform.ide.vfs.client.model.FolderModel;

/**
 * Storage for project properties.
 * TODO: Remove this class, when project notion will be created.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectProperties.java Sep 8, 2011 6:17:11 PM vereshchaka $
 */
public class ProjectProperties
{
   public interface ProjectType
   {
      public static final String SERVLET_JSP = "Servlet/JSP";
      
      public static final String SPRING = "Spring";
   }
   
   public interface Paas
   {
      public static final String CLOUDFOUNDRY = "CloudFoundry";
      
      public static final String CLOUDBEES = "CloudBees";
   }
   
   String name;
   
   String type;
   
   String paas;
   
   FolderModel parenFolder;
   
   public ProjectProperties()
   {
   }
   
   public ProjectProperties(String name)
   {
      this.name = name;
   }
   
   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @return the paas
    */
   public String getPaas()
   {
      return paas;
   }

   /**
    * @param paas the paas to set
    */
   public void setPaas(String paas)
   {
      this.paas = paas;
   }
   
   /**
    * @param parenFolder the parenFolder to set
    */
   public void setParenFolder(FolderModel parenFolder)
   {
      this.parenFolder = parenFolder;
   }
   
   /**
    * @return the parenFolder
    */
   public FolderModel getParenFolder()
   {
      return parenFolder;
   }

}
