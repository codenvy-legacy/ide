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

import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.Participant;
import com.google.collide.client.code.ParticipantModel.Listener;
import com.google.collide.shared.document.Document;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for displaying the collaborators.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ParticipantsPresenter.java Jan 30, 2013 3:18:56 PM azatsarynnyy $
 *
 */
public class ParticipantsPresenter implements ViewClosedHandler, EditorActiveFileChangedHandler,
   CollaborationDocumentLinkedHandler
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
      void setValue(List<Participant> value);
   }

   /**
    * Display.
    */
   private Display display;

   /**
    * Current active file.
    */
   private FileModel activeFile;

   /**
    * Current active document.
    */
   private Document activeDocument;

   /**
    * Map of {@link Document} id to the participant list.
    */
   private Map<Integer, List<Participant>> documentToParticipants = new HashMap<Integer, List<Participant>>();

   public ParticipantsPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(CollaborationDocumentLinkedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (activeFile == null || !(event.getEditor() instanceof CollabEditor))
      {
         activeDocument = null;
         closeView();
      }
      else
      {
         com.google.collide.client.editor.Editor editor = ((CollabEditor)event.getEditor()).getEditor();
         activeDocument = editor.getDocument();

         List<Participant> participants = documentToParticipants.get(activeDocument.getId());
         display.setValue(participants);

         // Don't show view if no any participants except for current user.
         if (participants != null && participants.size() > 1)
         {
            openView();
         }
         else
         {
            closeView();
         }
      }
   }

   /**
    * @see com.google.collide.client.collaboration.participants.CollaborationDocumentLinkedHandler#onDocumentLinked(com.google.collide.client.collaboration.participants.CollaborationDocumentLinkedEvent)
    */
   @Override
   public void onDocumentLinked(CollaborationDocumentLinkedEvent event)
   {
      final Document document = event.getDocument();
      event.getParticipantModel().addListener(new Listener()
      {

         @Override
         public void participantAdded(Participant participant)
         {
            List<Participant> participants = documentToParticipants.get(document.getId());
            if (participants == null)
            {
               participants = new ArrayList<Participant>();
               documentToParticipants.put(document.getId(), participants);
            }
            participants.add(participant);

            if (activeDocument != null && document.getId() == activeDocument.getId())
            {
               openView();
               display.setValue(participants);
            }
         }

         @Override
         public void participantRemoved(Participant participant)
         {
            List<Participant> participants = documentToParticipants.get(document.getId());
            if (participant == null)
            {
               return;
            }
            participants.remove(participant);

            if (activeDocument != null && document.getId() == activeDocument.getId())
            {
               display.setValue(participants);
               // Close view if no any participants except for current user.
               if (participants.size() <= 1)
               {
                  closeView();
               }
            }

         }
      });
   }

   /**
    * Open view.
    */
   private void openView()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
      }
   }

   /**
    * Close view.
    */
   private void closeView()
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
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

}
