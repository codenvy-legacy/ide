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
package org.exoplatform.ide.client.framework.vfs.callback;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback.MoveItemData;

/**
 * Callback the client has to implement to move item.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MoveItemCallback.java Feb 16, 2011 10:03:42 AM vereshchaka $
 *
 */
public abstract class MoveItemCallback extends AsyncRequestCallback<MoveItemData>
{

   public class MoveItemData
   {
      private Item item;
      
      private String oldHref;
      
      public MoveItemData(Item item, String href)
      {
         this.item = item;
         this.oldHref = href;
      }
      
      /**
       * @return the item
       */
      public Item getItem()
      {
         return item;
      }
      
      /**
       * @return the oldHref
       */
      public String getOldHref()
      {
         return oldHref;
      }
      
   }
   
   
   /**
    * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
    */
   @Override
   protected void onFailure(Throwable exception)
   {
      fireEvent(new ExceptionThrownEvent(
         "Service is not deployed.<br>Destination path does not exist<br>Folder already has item with same name."));
   }

}
