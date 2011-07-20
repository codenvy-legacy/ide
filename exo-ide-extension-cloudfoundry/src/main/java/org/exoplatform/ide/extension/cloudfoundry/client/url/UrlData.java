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
package org.exoplatform.ide.extension.cloudfoundry.client.url;

/**
 * Used as data for {@link RegisteredUrlsGrid}: wich URLs were selected for deletion.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UnmapUrlData.java Jul 19, 2011 11:09:01 AM vereshchaka $
 */
public class UrlData
{
   
   private boolean checked;
   
   private String url;
   
   public UrlData(String url, boolean checked)
   {
      this.url = url;
      this.checked = checked;
   }
   
   /**
    * @return the url
    */
   public String getUrl()
   {
      return url;
   }
   
   /**
    * @return the checked
    */
   public boolean isChecked()
   {
      return checked;
   }
   
   /**
    * @param checked the checked to set
    */
   public void setChecked(boolean checked)
   {
      this.checked = checked;
   }

}
