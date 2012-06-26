/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs to set the state of the App Engine user.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 23, 2012 10:27:32 AM anya $
 * 
 */
public class SetLoggedUserStateEvent extends GwtEvent<SetLoggedUserStateHandler>
{
   /**
    * Type used to register the event.
    */
   public static final GwtEvent.Type<SetLoggedUserStateHandler> TYPE = new GwtEvent.Type<SetLoggedUserStateHandler>();

   private boolean isLogged;

   /**
    * @param isLogged logged user state
    */
   public SetLoggedUserStateEvent(boolean isLogged)
   {
      this.isLogged = isLogged;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<SetLoggedUserStateHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(SetLoggedUserStateHandler handler)
   {
      handler.onSetLoggedUserState(this);
   }

   /**
    * @return the isLogged
    */
   public boolean isLogged()
   {
      return isLogged;
   }
}
