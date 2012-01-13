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
package org.exoplatform.ide.testframework.server.cloudbees;

import java.util.Map;

/**
 * Bean for Cloudbees applicaiton data.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudbeesApplication.java Aug 16, 2011 12:25:06 PM vereshchaka $
 * 
 */
public class CloudbeesApplication
{

   private String id;

   private String message;

   private String workDir;

   private String war;

   private Map<String, String> properties;

   public CloudbeesApplication()
   {
   }

   /**
    * @param id
    * @param message
    * @param workDir
    * @param war
    * @param properties
    */
   public CloudbeesApplication(String id, String message, String workDir, String war, Map<String, String> properties)
   {
      this.id = id;
      this.message = message;
      this.workDir = workDir;
      this.war = war;
      this.properties = properties;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the message
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * @param message the message to set
    */
   public void setMessage(String message)
   {
      this.message = message;
   }

   /**
    * @return the workDir
    */
   public String getWorkDir()
   {
      return workDir;
   }

   /**
    * @param workDir the workDir to set
    */
   public void setWorkDir(String workDir)
   {
      this.workDir = workDir;
   }

   /**
    * @return the war
    */
   public String getWar()
   {
      return war;
   }

   /**
    * @param war the war to set
    */
   public void setWar(String war)
   {
      this.war = war;
   }

   /**
    * @return the properties
    */
   public Map<String, String> getProperties()
   {
      return properties;
   }

   /**
    * @param properties the properties to set
    */
   public void setProperties(Map<String, String> properties)
   {
      this.properties = properties;
   }

}
