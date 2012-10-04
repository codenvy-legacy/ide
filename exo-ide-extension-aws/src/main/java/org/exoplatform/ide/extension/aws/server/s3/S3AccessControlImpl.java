/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.server.s3;

import org.exoplatform.ide.extension.aws.shared.s3.S3AccessControl;
import org.exoplatform.ide.extension.aws.shared.s3.S3IdentityType;
import org.exoplatform.ide.extension.aws.shared.s3.S3Permission;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3AccessControlImpl implements S3AccessControl
{
   private S3IdentityType identityType;
   private S3Permission permission;
   private String identifier;

   public S3AccessControlImpl()
   {
   }

   public S3AccessControlImpl(S3IdentityType identityType, S3Permission permission, String identifier)
   {
      this.identityType = identityType;
      this.permission = permission;
      this.identifier = identifier;
   }

   @Override
   public S3IdentityType getIdentityType()
   {
      return identityType;
   }

   @Override
   public void setIdentityType(S3IdentityType identityType)
   {
      this.identityType = identityType;
   }

   @Override
   public S3Permission getPermission()
   {
      return permission;
   }

   @Override
   public void setPermission(S3Permission permission)
   {
      this.permission = permission;
   }

   @Override
   public String getIdentifier()
   {
      return identifier;
   }

   @Override
   public void setIdentifier(String identifier)
   {
      this.identifier = identifier;
   }

   @Override
   public String toString()
   {
      return "S3AccessControlImpl{" +
         "identityType=" + identityType +
         ", permission=" + permission +
         ", identifier='" + identifier + '\'' +
         '}';
   }
}
