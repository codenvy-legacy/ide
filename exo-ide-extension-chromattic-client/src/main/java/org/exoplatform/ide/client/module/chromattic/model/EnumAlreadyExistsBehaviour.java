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
package org.exoplatform.ide.client.module.chromattic.model;

/**
 * The list of available operations to do if deploying node already exists.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public enum EnumAlreadyExistsBehaviour {
   IGNORE_IF_EXISTS(0, "ignore if exists"), FAIL_IF_EXISTS(2, "fail if exists"), REPLACE_IF_EXISTS(4, "replace if exists");

   /**
    * Behavior code.
    */
   private int code;

   /**
    * Behavior display name.
    */
   private String displayName;

   /**
    * @param code
    * @param displayName
    */
   EnumAlreadyExistsBehaviour(int code, String displayName)
   {
      this.code = code;
      this.displayName = displayName;
   }

   /**
    * @return the code
    */
   public int getCode()
   {
      return code;
   }

   /**
    * @return the displayName
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * Get {@link EnumAlreadyExistsBehaviour} by code value.
    * 
    * @param code code
    * @return {@link EnumAlreadyExistsBehaviour}
    */
   public static EnumAlreadyExistsBehaviour fromCode(int code)
   {
      for (EnumAlreadyExistsBehaviour alreadyExistsBehaviour : EnumAlreadyExistsBehaviour.values())
      {
         if (alreadyExistsBehaviour.getCode() == code)
         {
            return alreadyExistsBehaviour;
         }
      }
      throw new IllegalArgumentException(""+code);
   }
}
