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
package org.exoplatform.ide.client.framework.project;

/**
 * Defined languages.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 12:15:43 PM anya $
 * 
 */
public enum Language {
   PHP("PHP"), JAVA("Java"), JAVASCRIPT("JavaScript"), NODE_JS("Node.js"), PYTHON("Python"), RUBY("Ruby");

   private String value;

   private Language(String value)
   {
      this.value = value;
   }

   public String value()
   {
      return value;
   }

   /**
    * @param v project's type value
    * @return {@link ProjectType}
    */
   public static Language fromValue(String v)
   {
      for (Language c : Language.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }
}
