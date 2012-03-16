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
package org.exoplatform.ide.extension.openshift.server;

import org.exoplatform.ide.extension.openshift.shared.AppInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppInfoBean implements AppInfo
{
   private String name;

   private String type;

   private String gitUrl;

   private String publicUrl;

   private double creationTime;

   public AppInfoBean(String name, String type, String gitUrl, String publicUrl, long creationTime)
   {
      this.name = name;
      this.type = type;
      this.gitUrl = gitUrl;
      this.publicUrl = publicUrl;
      this.creationTime = creationTime;
   }

   public AppInfoBean()
   {
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getType()
    */
   @Override
   public String getType()
   {
      return type;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setType(java.lang.String)
    */
   @Override
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getGitUrl()
    */
   @Override
   public String getGitUrl()
   {
      return gitUrl;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setGitUrl(java.lang.String)
    */
   @Override
   public void setGitUrl(String gitUrl)
   {
      this.gitUrl = gitUrl;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getPublicUrl()
    */
   @Override
   public String getPublicUrl()
   {
      return publicUrl;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setPublicUrl(java.lang.String)
    */
   @Override
   public void setPublicUrl(String publicUrl)
   {
      this.publicUrl = publicUrl;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getCreationTime()
    */
   @Override
   public double getCreationTime()
   {
      return creationTime;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setCreationTime(double)
    */
   @Override
   public void setCreationTime(double creationTime)
   {
      this.creationTime = creationTime;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "AppInfo [name=" + name + ", type=" + type + ", gitUrl=" + gitUrl + ", publicUrl=" + publicUrl
         + ", creationTime=" + creationTime + "]";
   }
}
