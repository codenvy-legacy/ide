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
package org.exoplatform.ide.client.framework.vfs;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.VersionsCallback.VersionsData;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: VersionsCallback.java Feb 9, 2011 12:41:17 PM vereshchaka $
 *
 */
public abstract class VersionsCallback extends AsyncRequestCallback<VersionsData>
{
   
   public class VersionsData
   {
      private Item item;
      
      private List<Version> versions;
      
      public VersionsData(Item item, List<Version> versions)
      {
         this.item = item;
         this.versions = versions;
      }
      
      /**
       * @return the item
       */
      public Item getItem()
      {
         return item;
      }
      
      /**
       * @return the versions
       */
      public List<Version> getVersions()
      {
         return versions;
      }
   }
   
}
