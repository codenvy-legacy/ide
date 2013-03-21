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
package com.google.collide.server;

import com.google.collide.server.documents.EditSessions;
import com.google.collide.server.participants.Participants;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class FileEditSessionCleaner implements HttpSessionListener
{
   @Override
   public void sessionCreated(HttpSessionEvent se)
   {
   }

   @Override
   public void sessionDestroyed(HttpSessionEvent se)
   {
      ExoContainer container = getContainer();
      if (container != null)
      {
         Participants participants = (Participants)container.getComponentInstanceOfType(Participants.class);
         EditSessions editSessions = (EditSessions)container.getComponentInstanceOfType(EditSessions.class);
         final String id = se.getSession().getId();
         editSessions.closeAllSessions(id);
         participants.removeParticipant(id);
      }
   }

   protected ExoContainer getContainer()
   {
      return ExoContainerContext.getCurrentContainerIfPresent();
   }
}