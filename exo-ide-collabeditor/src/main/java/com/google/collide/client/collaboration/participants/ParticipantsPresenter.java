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

import com.google.collide.client.AppContext;
import com.google.collide.client.communication.MessageFilter;
import com.google.collide.client.document.DocumentManager;
import com.google.collide.dto.NewFileCollaborator;
import com.google.collide.dto.RoutingTypes;
import com.google.collide.shared.document.Document;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ParticipantsPresenter.java Jan 30, 2013 3:18:56 PM azatsarynnyy $
 *
 */
public class ParticipantsPresenter implements ViewClosedHandler, CollaborationEditorFileOpenedHandler, EditorActiveFileChangedHandler
{
   /**
    * View for participants panel.
    */
   public interface Display extends IsView
   {
      /**
       * Set participant list.
       * 
       * @param value participant list
       */
      void setValue(List<String> value);
   }

   private Display display;

   private AppContext appContext;

   private DocumentManager documentManager;

   /**
    * Current active file.
    */
   private FileModel activeFile;

   public ParticipantsPresenter(AppContext appContext, DocumentManager documentManager)
   {
      this.appContext = appContext;
      this.documentManager = documentManager;

      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(CollaborationEditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      this.appContext.getMessageFilter().registerMessageRecipient(RoutingTypes.NEWFILECOLLABORATOR,
         newFileCollaboratorMessageRecipient);
   }

   private final MessageFilter.MessageRecipient<NewFileCollaborator> newFileCollaboratorMessageRecipient =
      new MessageFilter.MessageRecipient<NewFileCollaborator>()
      {
         @Override
         public void onMessageReceived(NewFileCollaborator message)
         {
            addNewCollaborator(message);
         }
      };

   private void addNewCollaborator(NewFileCollaborator message)
   {
      Document document = documentManager.getDocumentByFilePath(message.getPath());

      IDE.fireEvent(new OutputEvent(String.valueOf(document.getId())));

      if (document != null)
      {
//         DocumentCollaborationController collaborationController =
//            docCollabControllersByDocumentId.get(document.getId());
//         collaborationController.getParticipantModel().addParticipant(true, message.getParticipant());
         List<String> list = new ArrayList<String>();
         list.add(message.getParticipant().getUserDetails().getGivenName());
         display.setValue(list);
      }
   }

   private void bindDisplay()
   {
      List<String> list = new ArrayList<String>();
      list.add("participant 1");
      list.add("participant 2");
      display.setValue(list);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();

//      if (activeFile == null || !MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType()))
      if (activeFile == null)
      {
         activeFile = null;
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      }
      else
      {
         if (display == null)
         {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
         }
         //         display.setValue(participantList);
      }
   }

   /**
    * @see com.google.collide.client.collaboration.participants.CollaborationEditorFileOpenedHandler#onEditorFileOpened(com.google.collide.client.collaboration.participants.CollaborationEditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(CollaborationEditorFileOpenedEvent event)
   {
//      event.getParticipantModel().getParticipants()
   }

}
