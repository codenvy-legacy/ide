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
package org.exoplatform.ide.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 30, 2010 $
 * 
 */
public class EnableStandartErrorsHandlingEvent extends GwtEvent<EnableStandartErrorsHandlingHandler>
{

   public static final GwtEvent.Type<EnableStandartErrorsHandlingHandler> TYPE =
      new GwtEvent.Type<EnableStandartErrorsHandlingHandler>();

   private boolean enable = true;

   public EnableStandartErrorsHandlingEvent()
   {
   }

   public EnableStandartErrorsHandlingEvent(boolean enable)
   {
      this.enable = enable;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EnableStandartErrorsHandlingHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(EnableStandartErrorsHandlingHandler handler)
   {
      handler.onEnableStandartErrorsHandling(this);
   }

   public boolean isEnable()
   {
      return enable;
   }
}
