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
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RHUserInfoImpl implements RHUserInfo
{
   private String rhcDomain;

   private String uuid;

   private String rhlogin;

   private String namespace;

   private List<AppInfo> apps;

   public RHUserInfoImpl(String rhcDomain, String uuid, String rhlogin, String namespace)
   {
      this.rhcDomain = rhcDomain;
      this.uuid = uuid;
      this.rhlogin = rhlogin;
      this.namespace = namespace;
   }

   public RHUserInfoImpl()
   {
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getRhcDomain()
    */
   @Override
   public String getRhcDomain()
   {
      return rhcDomain;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setRhcDomain(java.lang.String)
    */
   @Override
   public void setRhcDomain(String rhcDomain)
   {
      this.rhcDomain = rhcDomain;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getUuid()
    */
   @Override
   public String getUuid()
   {
      return uuid;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setUuid(java.lang.String)
    */
   @Override
   public void setUuid(String uuid)
   {
      this.uuid = uuid;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getRhlogin()
    */
   @Override
   public String getRhlogin()
   {
      return rhlogin;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setRhlogin(java.lang.String)
    */
   @Override
   public void setRhlogin(String rhlogin)
   {
      this.rhlogin = rhlogin;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getNamespace()
    */
   @Override
   public String getNamespace()
   {
      return namespace;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setNamespace(java.lang.String)
    */
   @Override
   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getApps()
    */
   @Override
   public List<AppInfo> getApps()
   {
      return apps;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setApps(java.util.List)
    */
   @Override
   public void setApps(List<AppInfo> apps)
   {
      this.apps = apps;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "RHUserInfoImpl{" +
         "rhcDomain='" + rhcDomain + '\'' +
         ", uuid='" + uuid + '\'' +
         ", rhlogin='" + rhlogin + '\'' +
         ", namespace='" + namespace + '\'' +
         ", apps=" + apps +
         '}';
   }
}
