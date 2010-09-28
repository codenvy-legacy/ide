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
package org.exoplatform.ide.client.module.vfs.api.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.LockToken;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 13, 2010 $
 *
 */
public class ItemLockResultReceivedEvent extends ServerExceptionEvent<ItemLockResultReceivedHandler>
{

   public static GwtEvent.Type<ItemLockResultReceivedHandler> TYPE = new Type<ItemLockResultReceivedHandler>();

   private LockToken lockToken;

   private Throwable exception;

   private Item item;

   private String errorMessage;

   /**
    * @param lockToken
    */
   public ItemLockResultReceivedEvent(Item item, LockToken lockToken, String errorMessage)
   {
      this.item = item;
      this.lockToken = lockToken;
      this.errorMessage = errorMessage;
   }

   @Override
   protected void dispatch(ItemLockResultReceivedHandler handler)
   {
      handler.onItemLockResultReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ItemLockResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the lockToken
    */
   public LockToken getLockToken()
   {
      return lockToken;
   }

   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent#setException(java.lang.Throwable)
    */
   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   public Throwable getException()
   {
      return exception;
   }

   /**
    * @return the errorMessage
    */
   public String getErrorMessage()
   {
      return errorMessage;
   }

}
