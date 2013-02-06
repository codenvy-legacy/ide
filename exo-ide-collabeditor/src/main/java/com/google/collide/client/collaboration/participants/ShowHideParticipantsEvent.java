/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.google.collide.client.collaboration.participants;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Listening this event IDE shows or hides participants of file edit.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideParticipantsEvent.java Feb 6, 2013 12:34:37 PM azatsarynnyy $
 *
 */
public class ShowHideParticipantsEvent extends GwtEvent<ShowHideParticipantsHandler>
{

   /**
    * Type class used to register events with the {@link HandlerManager}.
    */
   public static final GwtEvent.Type<ShowHideParticipantsHandler> TYPE =
      new GwtEvent.Type<ShowHideParticipantsHandler>();

   /**
    * Show or hide collaborators list?
    */
   private boolean show;

   /**
    * Creates a new event to show or hide collaborators list.
    * 
    * @param show if <code>true</code> - show collaborators list,
    *              if <code>false</code> - hide collaborators list
    */
   public ShowHideParticipantsEvent(boolean show)
   {
      this.show = show;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public Type<ShowHideParticipantsHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ShowHideParticipantsHandler handler)
   {
      handler.onShowHideParticipants(this);
   }

   /**
    * Show or hide collaborators list?
    * 
    * @return <code>true</code> - show collaborators list,
    *          <code>false</code> - hide collaborators list
    */
   public boolean isShow()
   {
      return show;
   }
}
