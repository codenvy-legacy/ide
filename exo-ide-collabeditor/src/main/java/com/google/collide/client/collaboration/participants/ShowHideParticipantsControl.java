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
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.Resources;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;

/**
 * Control for show or hide collaborators list.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideParticipantsControl.java Feb 6, 2013 12:34:48 PM azatsarynnyy $
 *
 */
public class ShowHideParticipantsControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   ViewClosedHandler, ViewOpenedHandler
{

   public static final String ID = CollabEditorExtension.LOCALIZATION_CONSTANT.collaboratorsControlId();

   public static final String TITLE = CollabEditorExtension.LOCALIZATION_CONSTANT.collaboratorsControlTitle();

   public static final String PROMPT_SHOW = CollabEditorExtension.LOCALIZATION_CONSTANT
      .collaboratorsControlPromptShow();

   public static final String PROMPT_HIDE = CollabEditorExtension.LOCALIZATION_CONSTANT
      .collaboratorsControlPromptHide();

   /**
    * Is Collaborators view opened?
    */
   private boolean collaboratorsViewOpened = false;

   /**
    * Creates new control instance. 
    * 
    * @param resources {@link Resource}
    */
   public ShowHideParticipantsControl(Resources resources)
   {
      super(ID);
      setTitle(TITLE);
      setImages(resources.getCollaboratorsImage(), resources.getCollaboratorsImage());
      setEvent(new ShowHideParticipantsEvent(true));
      setEnabled(true);
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ViewOpenedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null || !(event.getEditor() instanceof CollabEditor))
      {
         setVisible(false);
         return;
      }
      setVisible(true);
      updateState();
   }

   /**
    * Update control's state.
    */
   private void updateState()
   {
      setSelected(collaboratorsViewOpened);
      if (collaboratorsViewOpened)
      {
         setPrompt(PROMPT_HIDE);
         setEvent(new ShowHideParticipantsEvent(false));
      }
      else
      {
         setPrompt(PROMPT_SHOW);
         setEvent(new ShowHideParticipantsEvent(true));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof ParticipantsPresenter.Display)
      {
         collaboratorsViewOpened = true;
         updateState();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof ParticipantsPresenter.Display)
      {
         collaboratorsViewOpened = false;
         updateState();
      }
   }

}
