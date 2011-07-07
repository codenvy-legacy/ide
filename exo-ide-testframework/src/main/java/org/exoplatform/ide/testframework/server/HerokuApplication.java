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
package org.exoplatform.ide.testframework.server;

import java.util.HashMap;

/**
 * Bean for Heroku application's data.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 6, 2011 4:58:08 PM anya $
 *
 */
public class HerokuApplication
{
   /**
    * Application's name.
    */
   private String name;

   /**
    * Location of Git working directory.
    */
   private String gitUrl;

   /**
    * Remote repository name.
    */
   private String remoteName;

   /**
    * Application's properties.
    */
   private HashMap<String, String> properties;

   public HerokuApplication()
   {
   }

   /**
    * @param name application's name
    * @param gitUrl Git working directory location
    * @param remoteName remote repository name
    * @param properties application's properties
    */
   public HerokuApplication(String name, String gitUrl, String remoteName, HashMap<String, String> properties)
   {
      this.name = name;
      this.gitUrl = gitUrl;
      this.remoteName = remoteName;
      this.properties = properties;
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
    * @return the gitUrl
    */
   public String getGitUrl()
   {
      return gitUrl;
   }

   /**
    * @param gitUrl the gitUrl to set
    */
   public void setGitUrl(String gitUrl)
   {
      this.gitUrl = gitUrl;
   }

   /**
    * @return the remoteName
    */
   public String getRemoteName()
   {
      return remoteName;
   }

   /**
    * @param remoteName the remoteName to set
    */
   public void setRemoteName(String remoteName)
   {
      this.remoteName = remoteName;
   }

   /**
    * @return the properties
    */
   public HashMap<String, String> getProperties()
   {
      if (properties == null)
      {
         properties = new HashMap<String, String>();
      }
      return properties;
   }

   /**
    * @param properties the properties to set
    */
   public void setProperties(HashMap<String, String> properties)
   {
      this.properties = properties;
   }
}
