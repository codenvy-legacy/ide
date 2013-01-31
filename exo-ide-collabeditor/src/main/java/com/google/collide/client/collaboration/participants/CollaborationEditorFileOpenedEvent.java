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

import com.google.collide.client.code.ParticipantModel;
import com.google.collide.shared.document.Document;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CollaborationEditorFileOpenedEvent.java Jan 31, 2013 11:47:32 AM azatsarynnyy $
 *
 */

public class CollaborationEditorFileOpenedEvent extends GwtEvent<CollaborationEditorFileOpenedHandler>
{

   public static final GwtEvent.Type<CollaborationEditorFileOpenedHandler> TYPE =
      new GwtEvent.Type<CollaborationEditorFileOpenedHandler>();

   private Document document;

   private ParticipantModel participantModel;

   public CollaborationEditorFileOpenedEvent(Document document, ParticipantModel participantModel)
   {
      this.document = document;
      this.participantModel = participantModel;
   }

   public Document getDocument()
   {
      return document;
   }

   public ParticipantModel getParticipantModel()
   {
      return participantModel;
   }

   @Override
   protected void dispatch(CollaborationEditorFileOpenedHandler handler)
   {
      handler.onEditorFileOpened(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<CollaborationEditorFileOpenedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
