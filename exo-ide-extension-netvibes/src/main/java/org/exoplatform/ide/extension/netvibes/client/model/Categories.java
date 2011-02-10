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
package org.exoplatform.ide.extension.netvibes.client.model;

import java.util.LinkedHashMap;

/**
 * Available Netvibes widget's categories. 
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 *
 */
public class Categories
{
   /**
    * Map with available categories.
    */
   private LinkedHashMap<String, String> categoryMap;

   /**
    * Retrieve categories.
    * 
    * @return {@link LinkedHashMap} categories
    */
   public LinkedHashMap<String, String> getCategoryMap()
   {
      if (categoryMap == null)
      {
         categoryMap = new LinkedHashMap<String, String>();
      }
      return categoryMap;
   }

   /**
    * Set categories.
    * 
    * @param categoryMap categories
    */
   public void setCategoryMap(LinkedHashMap<String, String> categoryMap)
   {
      this.categoryMap = categoryMap;
   }
}
