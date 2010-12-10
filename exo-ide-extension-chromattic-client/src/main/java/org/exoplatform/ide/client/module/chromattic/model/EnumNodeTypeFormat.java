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
 * Available node type's formats.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public enum EnumNodeTypeFormat {
   EXO, CND;

   /**
    * @return {@link String} value
    */
   public String value()
   {
      return name();
   }

   /**
    * @return {@link String[]} get values
    */
   public static String[] getValues()
   {
      String[] values = new String[values().length];
      int i = 0;
      for (EnumNodeTypeFormat format : values())
      {
         values[i] = format.name();
         i++;
      }
      return values;
   }
}
