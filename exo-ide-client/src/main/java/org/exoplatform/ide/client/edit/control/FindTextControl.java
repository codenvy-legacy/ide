/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.edit.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.edit.FindTextPresenter;
import org.exoplatform.ide.client.edit.event.FindTextEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.editor.api.EditorCapability;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
@RolesAllowed({"administrators", "developers"})
public class FindTextControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   ViewOpenedHandler, ViewClosedHandler
{
   //   public static final String ID = "Edit/Find&#47Replace...";
   public static final String ID = "Edit/Find-Replace...";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.findReplaceControl();

   private boolean findTextViewOpened = false;

   public FindTextControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.findText(), IDEImageBundle.INSTANCE.findTextDisabled());
      setEvent(new FindTextEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null
         || !event.getEditor().isCapable(EditorCapability.FIND_AND_REPLACE))
      {
         setVisible(false);
         setEnabled(false);
         return;
      }
      else
      {
         setVisible(true);
      }

      if (event.getEditor().isReadOnly())
      {
         setEnabled(false);
         return;
      }

      boolean canFindReplace = event.getEditor().isCapable(EditorCapability.FIND_AND_REPLACE);
      //boolean isOpened = openedForms.contains(FindTextForm.ID); 
      boolean enableSearch = canFindReplace && !findTextViewOpened;
      setEnabled(enableSearch);
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof FindTextPresenter.Display)
      {
         findTextViewOpened = true;
         setEnabled(false);
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof FindTextPresenter.Display)
      {
         findTextViewOpened = false;
         setEnabled(true);
      }
   }

}
