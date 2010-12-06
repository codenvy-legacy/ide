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
package org.exoplatform.ide.vfs.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Describe virtual file system and its capabilities.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VirtualFileSystemInfo
{
   public enum ACLCapability {
      NONE("none"), READ("read"), WRITE("write");

      private final String value;

      private ACLCapability(String value)
      {
         this.value = value;
      }

      /**
       * @return value of ACLCapabilities
       */
      public String value()
      {
         return value;
      }

      /**
       * Get ACLCapabilities instance from string value.
       * 
       * @param value string value
       * @return ACLCapabilities
       * @throws IllegalArgumentException if there is no corresponded
       *            ACLCapabilities for specified <code>value</code>
       */
      public static ACLCapability fromValue(String value)
      {
         for (ACLCapability e : ACLCapability.values())
            if (e.value.equals(value))
               return e;
         throw new IllegalArgumentException(value);
      }

      /**
       * @see java.lang.Enum#toString()
       */
      public String toString()
      {
         return value;
      }
   }

   public enum QueryCapability {
      NONE("none"), METADATAONLY("metadataonly"), FULLTEXTONLY("fulltextonly"), BOTHSEPARATE("bothseparate"), BOTHCOMBINED(
         "bothcombined");

      private final String value;

      private QueryCapability(String value)
      {
         this.value = value;
      }

      /**
       * @return value of QueryCapability
       */
      public String value()
      {
         return value;
      }

      /**
       * Get QueryCapability instance from string value.
       * 
       * @param value string value
       * @return QueryCapability
       * @throws IllegalArgumentException if there is no corresponded
       *            QueryCapability for specified <code>value</code>
       */
      public static QueryCapability fromValue(String value)
      {
         for (QueryCapability e : QueryCapability.values())
            if (e.value.equals(value))
               return e;
         throw new IllegalArgumentException(value);
      }

      /**
       * @see java.lang.Enum#toString()
       */
      @Override
      public String toString()
      {
         return value;
      }
   }

   public enum BasicPermissions {
      READ("read"), WRITE("write"), ALL("all");

      private final String value;

      private BasicPermissions(String value)
      {
         this.value = value;
      }

      /**
       * @return value of BasicPermissions
       */
      public String value()
      {
         return value;
      }

      /**
       * Get BasicPermissions instance from string value.
       * 
       * @param value string value
       * @return BasicPermissions
       * @throws IllegalArgumentException if there is no corresponded
       *            BasicPermissions for specified <code>value</code>
       */
      public static BasicPermissions fromValue(String value)
      {
         for (BasicPermissions e : BasicPermissions.values())
            if (e.value.equals(value))
               return e;
         throw new IllegalArgumentException(value);
      }

      /**
       * @see java.lang.Enum#toString()
       */
      @Override
      public String toString()
      {
         return value;
      }
   }

   public static final String ANONYMOUS_PRINCIPAL = "anonymous";

   private boolean versioningSupported;

   private boolean lockSupported;

   private String anonymousPrincipal;

   private Collection<String> permissions;

   private ACLCapability aclCapability;

   private QueryCapability queryCapability;

   public VirtualFileSystemInfo(boolean versioningSupported, boolean lockSupported, String anonymousPrincipal,
      Collection<String> permissions, ACLCapability aclCapability, QueryCapability queryCapability)
   {
      this.versioningSupported = versioningSupported;
      this.lockSupported = lockSupported;
      this.anonymousPrincipal = anonymousPrincipal;
      this.permissions = permissions;
      this.aclCapability = aclCapability;
      this.queryCapability = queryCapability;
   }

   public VirtualFileSystemInfo()
   {
      this(false, false, ANONYMOUS_PRINCIPAL, new ArrayList<String>(), ACLCapability.NONE, QueryCapability.NONE);
   }

   /**
    * @return the versioningSupported
    */
   public boolean isVersioningSupported()
   {
      return versioningSupported;
   }

   /**
    * @param versioningSupported the versioningSupported to set
    */
   public void setVersioningSupported(boolean versioningSupported)
   {
      this.versioningSupported = versioningSupported;
   }

   /**
    * @return the lockSupported
    */
   public boolean isLockSupported()
   {
      return lockSupported;
   }

   /**
    * @param lockSupported the lockSupported to set
    */
   public void setLockSupported(boolean lockSupported)
   {
      this.lockSupported = lockSupported;
   }

   /**
    * @return the anonymousPrincipal
    */
   public String getAnonymousPrincipal()
   {
      return anonymousPrincipal;
   }

   /**
    * @param anonymousPrincipal the anonymousPrincipal to set
    */
   public void setAnonymousPrincipal(String anonymousPrincipal)
   {
      this.anonymousPrincipal = anonymousPrincipal;
   }

   /**
    * @return the permissions
    */
   public Collection<String> getPermissions()
   {
      return permissions;
   }

   /**
    * @param permissions the permissions to set
    */
   public void setPermissions(Collection<String> permissions)
   {
      this.permissions = permissions;
   }

   /**
    * @return the aclCapability
    */
   public ACLCapability getAclCapability()
   {
      return aclCapability;
   }

   /**
    * @param aclCapability the aclCapability to set
    */
   public void setAclCapability(ACLCapability aclCapability)
   {
      this.aclCapability = aclCapability;
   }

   /**
    * @return the queryCapability
    */
   public QueryCapability getQueryCapability()
   {
      return queryCapability;
   }

   /**
    * @param queryCapability the queryCapability to set
    */
   public void setQueryCapability(QueryCapability queryCapability)
   {
      this.queryCapability = queryCapability;
   }
}
