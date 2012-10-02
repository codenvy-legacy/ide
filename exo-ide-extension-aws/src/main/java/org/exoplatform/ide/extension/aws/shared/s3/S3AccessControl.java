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
package org.exoplatform.ide.extension.aws.shared.s3;

/**
 * Represent information about user identify type and permission given for it.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3AccessControl
{
   /**
    * Get type of identity e.g. uri, email or canonical name.
    *
    * @return
    *    identity type
    */
   S3IdentityType getIdentityType();

   /**
    * Set type of identity.
    *
    * @param identityType
    *    identity type
    */
   void setIdentityType(S3IdentityType identityType);

   /**
    * Get permission given for this user.
    *
    * @return
    *    type of permission
    */
   S3Permission getPermission();

   /**
    * Set type of permission for this user.
    *
    * @param permission
    *    type of permission
    */
   void setPermission(S3Permission permission);

   /**
    * Get user identity value. e.g. "John", "admin@site.com" or "http://acs.amazonaws.com/groups/global/AllUsers".
    *
    * @return
    *    value of the user identity
    */
   String getIdentifier();

   /**
    * Set user identity value.
    *
    * @param identifier
    *    value of the user identity
    */
   void setIdentifier(String identifier);
}
