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
package org.exoplatform.ide.client.module.development.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.outline.OutlineForm;
import org.exoplatform.ide.client.outline.OutlineTreeGrid;
import org.exoplatform.ide.client.panel.event.PanelClosedEvent;
import org.exoplatform.ide.client.panel.event.PanelClosedHandler;
import org.exoplatform.ide.client.panel.event.PanelOpenedEvent;
import org.exoplatform.ide.client.panel.event.PanelOpenedHandler;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class ShowOutlineControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   PanelClosedHandler, PanelOpenedHandler
{

   public static final String ID = "View/Show \\ Hide Outline";

   public static final String TITLE = "Outline";

   public static final String PROMPT_SHOW = "Show Outline";

   public static final String PROMPT_HIDE = "Hide Outline";

   private boolean outLineFormOpened = false;

   public ShowOutlineControl()
   {
      super(ID);
      setTitle(TITLE);
      setImages(IDEImageBundle.INSTANCE.outline(), IDEImageBundle.INSTANCE.outlineDisabled());
      setEvent(new ShowOutlineEvent(true));
      setEnabled(true);
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(PanelClosedEvent.TYPE, this);
      eventBus.addHandler(PanelOpenedEvent.TYPE, this);
   }
   
   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null || !event.getEditor().canCreateTokenList())
      {
         setVisible(false);
         return;
      }

      boolean visible = OutlineTreeGrid.haveOutline(event.getFile());
      setVisible(visible);
      if (visible)
      {
         update();
      }
   }

   private void update()
   {
      setSelected(outLineFormOpened);

      if (outLineFormOpened)
      {
         setPrompt(PROMPT_HIDE);
         setEvent(new ShowOutlineEvent(false));
      }
      else
      {
         setPrompt(PROMPT_SHOW);
         setEvent(new ShowOutlineEvent(true));
      }
   }

   public void onPanelOpened(PanelOpenedEvent event)
   {
      if (OutlineForm.ID.equals(event.getPanelId()))
      {
         setSelected(true);
         outLineFormOpened = true;
         update();
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelClosedHandler#onPanelClosed(org.exoplatform.ide.client.panel.event.PanelClosedEvent)
    */
   public void onPanelClosed(PanelClosedEvent event)
   {
      if (OutlineForm.ID.equals(event.getPanelId()))
      {
         setSelected(false);
         outLineFormOpened = false;
         update();
      }
   }

}
