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
package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import com.google.gwt.i18n.client.DateTimeFormat;

import org.exoplatform.ide.client.framework.vfs.Version;

import java.util.Comparator;
import java.util.Date;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 30, 2010 $
 *
 */
public class VersionsByDateComparator implements Comparator<Version>
{
   /**
    * Z format.
    */
   private static final String Z_FORMAT = "(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})[zZ]";

   /**
    * Z format pattern.
    */
   private static final String Z_FORMAT_PATTERN = "yyyy-MM-dd'T'hh:mm:ss'Z'";

   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   public int compare(Version version1, Version version2)
   {
      String dateStr1 = version1.getCreationDate().toUpperCase();
      String dateStr2 = version2.getCreationDate().toUpperCase();
      Date date1 = new Date();
      Date date2 = new Date();
      if (dateStr1.matches(Z_FORMAT))
      {
         DateTimeFormat dateFormat = DateTimeFormat.getFormat(Z_FORMAT_PATTERN);
         date1 = dateFormat.parse(dateStr1);
      }
      if (dateStr2.matches(Z_FORMAT))
      {
         DateTimeFormat dateFormat = DateTimeFormat.getFormat(Z_FORMAT_PATTERN);
         date2 = dateFormat.parse(dateStr2);
      }

      if (date2.compareTo(date1) == 0)
      {
         return version2.getDisplayName().compareTo(version1.getDisplayName());
      }
      return date2.compareTo(date1);
   }
}
