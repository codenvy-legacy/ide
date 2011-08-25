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
package org.exoplatform.ide.testframework.server.jenkins;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Job.java Aug 23, 2011 12:18:16 PM vereshchaka $
 *
 */
public class Job
{
   private String name;
   private String buildUrl;
   private String statusUrl;

   public Job(String name, String buildUrl, String statusUrl)
   {
      this.name = name;
      this.buildUrl = buildUrl;
      this.statusUrl = statusUrl;
   }

   public Job()
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

   public String getBuildUrl()
   {
      return buildUrl;
   }

   public void setBuildUrl(String buildUrl)
   {
      this.buildUrl = buildUrl;
   }

   public String getStatusUrl()
   {
      return statusUrl;
   }

   public void setStatusUrl(String statusUrl)
   {
      this.statusUrl = statusUrl;
   }
}
