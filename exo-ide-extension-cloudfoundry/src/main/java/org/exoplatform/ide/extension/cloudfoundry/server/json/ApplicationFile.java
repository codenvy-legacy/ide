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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationFile
{
   private long size;
   private String sha1;
   private String fn;

   public ApplicationFile(long size, String sha1, String fn)
   {
      this.size = size;
      this.sha1 = sha1;
      this.fn = fn;
   }

   public ApplicationFile()
   {
   }

   public long getSize()
   {
      return size;
   }

   public void setSize(long size)
   {
      this.size = size;
   }

   public String getSha1()
   {
      return sha1;
   }

   public void setSha1(String sha1)
   {
      this.sha1 = sha1;
   }

   public String getFn()
   {
      return fn;
   }

   public void setFn(String fn)
   {
      this.fn = fn;
   }

   @Override
   public String toString()
   {
      return "ApplicationFile [size=" + size + ", sha1=" + sha1 + ", fn=" + fn + "]";
   }
}
