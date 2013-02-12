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
package com.google.collide.client.collaboration;

import com.codenvy.ide.notification.Notification;
import com.codenvy.ide.notification.NotificationManager;
import com.google.collide.client.collaboration.CollaborationManager.ParticipantsListener;
import com.google.collide.client.communication.MessageFilter;
import com.google.collide.client.communication.MessageFilter.MessageRecipient;
import com.google.collide.dto.FileOperationNotification;
import com.google.collide.dto.RoutingTypes;
import com.google.collide.dto.UserDetails;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NotificationController implements ParticipantsListener
{


   public static final int DURATION = 3000;

   private MessageRecipient<FileOperationNotification> fileOperationNotificationRecipient = new MessageRecipient<FileOperationNotification>()
   {
      @Override
      public void onMessageReceived(FileOperationNotification message)
      {
         showFileOperationNotification(message);
      }
   };

   private void showFileOperationNotification(FileOperationNotification notification)
   {
      manager.addNotification(new Notification("User " + notification.getUserId(), 10000));
   }

   private NotificationManager manager;

   private CollaborationManager collaborationManager;

   public NotificationController(NotificationManager manager, CollaborationManager collaborationManager,  MessageFilter messageFilter)
   {
      this.collaborationManager = collaborationManager;
      collaborationManager.getParticipantsListenerManager().add(this);
      this.manager = manager;
      messageFilter.registerMessageRecipient(RoutingTypes.FILEOPERATIONNOTIFICATION, fileOperationNotificationRecipient);
   }

   @Override
   public void userOpenFile(String path, UserDetails user)
   {
      manager.addNotification(
         new Notification("User <b>" + user.getDisplayName() + "</b> open file: " + path, DURATION));
   }

   @Override
   public void userCloseFile(String path, UserDetails user)
   {
      manager.addNotification(
         new Notification("User <b>" + user.getDisplayName() + "</b> close file: " + path, DURATION));
   }
}
