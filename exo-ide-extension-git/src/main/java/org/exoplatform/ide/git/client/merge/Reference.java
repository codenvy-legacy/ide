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
package org.exoplatform.ide.git.client.merge;

/**
 * Git reference bean.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 20, 2011 2:41:39 PM anya $
 *
 */
public class Reference
{
   enum RefType {
      LOCAL_BRANCH, REMOTE_BRANCH, TAG;
   }

   /**
    * Short name of the reference to display.
    */
   private String displayName;

   /**
    * Full name of the reference.
    */
   private String fullName;

   /**
    * Type of the reference.
    */
   private RefType refType;

   /**
    * @param fullName full name of the reference
    * @param displayName short name of the reference to display
    * @param refType type the reference
    */
   public Reference(String fullName, String displayName, RefType refType)
   {
      this.displayName = displayName;
      this.fullName = fullName;
      this.refType = refType;
   }

   /**
    * @return the displayName
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @return the fullName
    */
   public String getFullName()
   {
      return fullName;
   }

   /**
    * @return the refType
    */
   public RefType getRefType()
   {
      return refType;
   }
}
