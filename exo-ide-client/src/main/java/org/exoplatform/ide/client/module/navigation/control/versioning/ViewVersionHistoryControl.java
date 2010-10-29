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
package org.exoplatform.ide.client.module.navigation.control.versioning;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionEvent;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.panel.event.PanelClosedEvent;
import org.exoplatform.ide.client.panel.event.PanelClosedHandler;
import org.exoplatform.ide.client.panel.event.PanelOpenedEvent;
import org.exoplatform.ide.client.panel.event.PanelOpenedHandler;
import org.exoplatform.ide.client.versioning.VersionContentForm;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class ViewVersionHistoryControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler, PanelClosedHandler, PanelOpenedHandler
{

   private static final String ID = "View/Version History...";

   private final String TITLE = "Version History...";

   private final String PROMPT_SHOW = "View Item Version History";
   
   private final String PROMPT_HIDE = "Hide Item Version History";
   
   private boolean versionPanelOpened = false;

   /**
    * @param id
    * @param eventBus
    */
   public ViewVersionHistoryControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT_SHOW);
      setEvent(new OpenVersionEvent(true));
      setImages(IDEImageBundle.INSTANCE.viewVersionContent(), IDEImageBundle.INSTANCE.viewVersionContentDisabled());
      setDelimiterBefore(true);
      setCanBeSelected(true);
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(PanelClosedEvent.TYPE, this);
      eventBus.addHandler(PanelOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getFile().isNewFile() || event.getFile() instanceof Version)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }
      setVisible(true);
      setEnabled(true);
   }
   
   private void update()
   {
      setSelected(versionPanelOpened);

      if (versionPanelOpened)
      {
         setPrompt(PROMPT_HIDE);
         setEvent(new OpenVersionEvent(false));
      }
      else
      {
         setPrompt(PROMPT_SHOW);
         setEvent(new OpenVersionEvent(true));
      }
   }

   public void onPanelOpened(PanelOpenedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         setSelected(true);
         versionPanelOpened = true;
         update();
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelClosedHandler#onPanelClosed(org.exoplatform.ide.client.panel.event.PanelClosedEvent)
    */
   public void onPanelClosed(PanelClosedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         setSelected(false);
         versionPanelOpened = false;
         update();
      }
   }
}
