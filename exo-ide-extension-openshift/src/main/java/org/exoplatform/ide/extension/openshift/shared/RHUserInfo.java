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
package org.exoplatform.ide.extension.openshift.shared;

import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RHUserInfo
{
   private String rhcDomain;
   private String uuid;
   private String rhlogin;
   private String namespace;
   private List<AppInfo> apps;

   public RHUserInfo(String rhcDomain, String uuid, String rhlogin, String namespace)
   {
      this.rhcDomain = rhcDomain;
      this.uuid = uuid;
      this.rhlogin = rhlogin;
      this.namespace = namespace;
   }

   public RHUserInfo()
   {
   }

   public String getRhcDomain()
   {
      return rhcDomain;
   }

   public void setRhcDomain(String rhcDomain)
   {
      this.rhcDomain = rhcDomain;
   }

   public String getUuid()
   {
      return uuid;
   }

   public void setUuid(String uuid)
   {
      this.uuid = uuid;
   }

   public String getRhlogin()
   {
      return rhlogin;
   }

   public void setRhlogin(String rhlogin)
   {
      this.rhlogin = rhlogin;
   }

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   public List<AppInfo> getApps()
   {
      return apps;
   }

   public void setApps(List<AppInfo> apps)
   {
      this.apps = apps;
   }

   @Override
   public String toString()
   {
      return "RHUserInfo [rhcDomain=" + rhcDomain + ", uuid=" + uuid + ", rhlogin=" + rhlogin + ", namespace="
         + namespace + "]";
   }
}
