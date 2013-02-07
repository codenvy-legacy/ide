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

import com.google.gwt.event.shared.EventHandler;

/**
 * {@link EventHandler} for listening {@link ShowHideParticipantsEvent}s.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideParticipantsHandler.java Feb 6, 2013 12:34:27 PM azatsarynnyy $
 *
 */
public interface ShowHideParticipantsHandler extends EventHandler
{
   /**
    * @param event {@link ShowHideParticipantsEvent}
    */
   void onShowHideParticipants(ShowHideParticipantsEvent event);
}
