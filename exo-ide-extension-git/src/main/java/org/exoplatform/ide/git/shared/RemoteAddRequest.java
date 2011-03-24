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
package org.exoplatform.ide.git.shared;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteAddRequest extends GitRequest
{
   private String name;
   private String url;
   private String[] pushUrls;
   private String[] branches;

   /**
    * @param name
    * @param url
    * @param pushUrls
    * @param branches
    */
   public RemoteAddRequest(String name, String url, String[] pushUrls, String[] branches)
   {
      this.name = name;
      this.url = url;
      this.pushUrls = pushUrls;
      this.branches = branches;
   }

   /**
    * @param name
    * @param url
    */
   public RemoteAddRequest(String name, String url)
   {
      this.name = name;
      this.url = url;
   }

   /**
    * 
    */
   public RemoteAddRequest()
   {
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getUrl()
   {
      return url;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }

   public String[] getPushUrls()
   {
      return pushUrls;
   }

   public void setPushUrls(String[] pushUrls)
   {
      this.pushUrls = pushUrls;
   }

   public String[] getBranches()
   {
      return branches;
   }

   public void setBranches(String[] branches)
   {
      this.branches = branches;
   }
}
