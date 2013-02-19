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
import com.codenvy.ide.notification.Notification.NotificationType;
import com.codenvy.ide.notification.NotificationManager;
import com.codenvy.ide.users.UsersModel;
import com.google.collide.client.collaboration.CollaborationManager.ParticipantsListener;
import com.google.collide.client.common.BaseResources.Css;
import com.google.collide.client.communication.MessageFilter;
import com.google.collide.client.communication.MessageFilter.MessageRecipient;
import com.google.collide.client.util.Elements;
import com.google.collide.client.util.logging.Log;
import com.google.collide.dto.FileOperationNotification;
import com.google.collide.dto.FileOperationNotification.Operation;
import com.google.collide.dto.RoutingTypes;
import com.google.collide.dto.UserDetails;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HTML;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AnchorElement;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NotificationController implements ParticipantsListener, ProjectOpenedHandler, ProjectClosedHandler
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


   private NotificationManager manager;

   private UsersModel usersModel;

   private HandlerManager eventBus;

   private Css css;

   private ProjectModel project;

   public NotificationController(NotificationManager manager, CollaborationManager collaborationManager,
      MessageFilter messageFilter, UsersModel usersModel, HandlerManager eventBus, Css css)
   {
      this.usersModel = usersModel;
      this.eventBus = eventBus;
      this.css = css;
      collaborationManager.getParticipantsListenerManager().add(this);
      this.manager = manager;
      messageFilter.registerMessageRecipient(RoutingTypes.FILEOPERATIONNOTIFICATION,
         fileOperationNotificationRecipient);
      eventBus.addHandler(ProjectOpenedEvent.TYPE, this);
      eventBus.addHandler(ProjectClosedEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void userOpenFile(final String path, UserDetails user)
   {
      if (isShow(path))
      {
         HTML html = new HTML("User <b>" + user.getDisplayName() + "</b> open file: ");
         AnchorElement anchorElement = getAnchorElement(path);
         html.getElement().appendChild((Node)anchorElement);
         manager.addNotification(new Notification(html, NotificationType.INFO, DURATION));
      }
   }

   private AnchorElement getAnchorElement(final String path)
   {
      AnchorElement anchorElement = Elements.createAnchorElement(css.anchor());
      anchorElement.setHref("javascript:;");
      anchorElement.setTextContent(path);
      anchorElement.addEventListener(Event.CLICK, new EventListener()
      {
         @Override
         public void handleEvent(Event event)
         {
            openFile(path);
         }
      }, false);
      return anchorElement;
   }

   private void openFile(String path)
   {
      try
      {
         VirtualFileSystem.getInstance().getItemByPath(path, new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
         {
            @Override
            protected void onSuccess(ItemWrapper result)
            {
               if(result.getItem() != null && result.getItem() instanceof FileModel)
               {
                  FileModel file = (FileModel)result.getItem();
                  file.setProject(project);
                  eventBus.fireEvent(new OpenFileEvent(file));
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Log.error(AsyncRequestCallback.class, exception);
            }
         });
      }
      catch (RequestException e)
      {
         Log.error(AsyncRequestCallback.class, e);
      }
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public void userCloseFile(final String path, UserDetails user)
   {
      if (isShow(path))
      {
         HTML html = new HTML("User <b>" + user.getDisplayName() + "</b> close file: ");
         AnchorElement anchorElement = getAnchorElement(path);
         html.getElement().appendChild((Node)anchorElement);
         manager.addNotification(new Notification(html, NotificationType.INFO, DURATION));
      }
   }

   private boolean isShow(String path)
   {
      if (project == null)
      {
         return true;
      }

      path = path.substring(1);
      path = path.substring(0, path.indexOf('/'));
      if (path.equals(project.getName()))
      {
         return true;
      }
      return false;
   }

   private void showFileOperationNotification(FileOperationNotification notification)
   {
      UserDetails user = usersModel.getUserById(notification.getUserId());
      String targetPaht = notification.getTarget();
      targetPaht = targetPaht.substring(targetPaht.lastIndexOf('/') + 1, targetPaht.length());
      String fileName = notification.getFilePath();
      fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());
      manager.addNotification(new Notification("User <b>" + user.getDisplayName() + "</b> wont to " + getOperationName(
         notification.getOperation()) + " <b>" + targetPaht + "</b> and ask you to close file <b>" + fileName + "</b>",
         NotificationType.MESSAGE, -1));
   }

   private String getOperationName(Operation operation)
   {
      switch (operation)
      {
         case RENAME:
            return "rename";
         case DELETE:
            return "delete";
         case MOVE:
            return "move";
         case REFACTORING:
            return "perform refactoring";

         default:
            return operation.name().toLowerCase();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      project = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
   }
}
