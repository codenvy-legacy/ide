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
package org.exoplatform.ide.extension.heroku.client.login;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs after user's logged in action. 
 * If it ends with fail, then {{@link #isFailed()}} returns <code>true</code>.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 31, 2011 11:15:36 AM anya $
 *
 */
public class LoggedInEvent extends GwtEvent<LoggedInHandler>
{
   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<LoggedInHandler> TYPE = new GwtEvent.Type<LoggedInHandler>();
   
   /**
    * If <code>true</code> log in failed.
    */
   private boolean isFailed;
   
   /**
    * @param isFailed if <code>true</code> log in failed
    */
   public LoggedInEvent(boolean isFailed)
   {
      this.isFailed = isFailed;
   }
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<LoggedInHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(LoggedInHandler handler)
   {
      handler.onLoggedIn(this);
   }

   /**
    * @return if <code>true</code> log in failed
    */
   public boolean isFailed()
   {
      return isFailed;
   }
}
