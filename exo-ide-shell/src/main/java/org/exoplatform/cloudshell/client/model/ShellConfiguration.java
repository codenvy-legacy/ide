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
package org.exoplatform.cloudshell.client.model;

import org.exoplatform.ide.client.framework.userinfo.UserInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 12, 2011 evgen $
 *
 */
public class ShellConfiguration
{
   private UserInfo userInfo;

   private String entryPoint;

   /**
    * 
    */
   public ShellConfiguration()
   {
   }

   /**
    * @param userInfo
    * @param entryPoint
    */
   public ShellConfiguration(UserInfo userInfo, String entryPoint)
   {
      super();
      this.userInfo = userInfo;
      this.entryPoint = entryPoint;
   }

   /**
    * @return the userInfo
    */
   public UserInfo getUserInfo()
   {
      return userInfo;
   }

   /**
    * @param userInfo the userInfo to set
    */
   public void setUserInfo(UserInfo userInfo)
   {
      this.userInfo = userInfo;
   }

   /**
    * @return the entryPoint
    */
   public String getEntryPoint()
   {
      return entryPoint;
   }

   /**
    * @param entryPoint the entryPoint to set
    */
   public void setEntryPoint(String entryPoint)
   {
      this.entryPoint = entryPoint;
   }

}
