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
import com.google.collide.client.Resources;
import com.google.collide.client.code.Participant;
import com.google.collide.client.code.ParticipantModel.Listener;
import com.google.collide.client.editor.Editor;
import com.google.collide.shared.document.Document;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

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
public class ParticipantsPresenter implements ShowHideParticipantsHandler, ViewClosedHandler,
   EditorActiveFileChangedHandler, CollaborationDocumentLinkedHandler
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
    * Current active document.
    */
   private Document activeDocument;

   /**
    * Participants list for active file.
    */
   private List<Participant> participantsList = new ArrayList<Participant>();

   /**
    * Map of {@link Document} id to the participant list.
    */
   private Map<Integer, List<Participant>> documentToParticipants = new HashMap<Integer, List<Participant>>();

   /**
    * Creates new instance of {@link ParticipantsPresenter}.
    *
    * @param resources {@link Resources}
    */
   public ParticipantsPresenter(Resources resources)
   {
      IDE.addHandler(ShowHideParticipantsEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(CollaborationDocumentLinkedEvent.TYPE, this);

      IDE.getInstance().addControl(new ShowHideParticipantsControl(resources), Docking.TOOLBAR);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      org.exoplatform.ide.editor.client.api.Editor activeEditor = event.getEditor();
      if (event.getFile() == null || activeEditor == null || !(activeEditor instanceof CollabEditor))
      {
         activeDocument = null;
         closeView();
         return;
      }

      Editor editor = ((CollabEditor)activeEditor).getEditor();
      activeDocument = editor.getDocument();
      participantsList = documentToParticipants.get(activeDocument.getId());
      if (participantsList == null)
      {
         participantsList = new ArrayList<Participant>();
      }

      if (display != null)
      {
         display.setValue(participantsList);
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
            participantsList = documentToParticipants.get(document.getId());
            if (participantsList == null)
            {
               participantsList = new ArrayList<Participant>();
               documentToParticipants.put(document.getId(), participantsList);
            }
            participantsList.add(participant);

            if (display != null && activeDocument != null && document.getId() == activeDocument.getId())
            {
               display.setValue(participantsList);
            }
         }

         @Override
         public void participantRemoved(Participant participant)
         {
            participantsList = documentToParticipants.get(document.getId());
            if (participant == null)
            {
               closeView();
               return;
            }
            participantsList.remove(participant);

            if (display != null && activeDocument != null && document.getId() == activeDocument.getId())
            {
               display.setValue(participantsList);
            }
         }
      });
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

   /**
    * @see com.google.collide.client.collaboration.participants.ShowHideParticipantsHandler#onShowHideParticipants(com.google.collide.client.collaboration.participants.ShowHideParticipantsEvent)
    */
   @Override
   public void onShowHideParticipants(ShowHideParticipantsEvent event)
   {
      if (!event.isShow())
      {
         closeView();
      }
      else if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         display.setValue(participantsList);
      }
   }
}
