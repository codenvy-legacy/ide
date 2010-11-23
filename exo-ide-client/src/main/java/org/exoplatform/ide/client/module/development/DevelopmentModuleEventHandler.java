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
package org.exoplatform.ide.client.module.development;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.module.development.event.ShowOutlineHandler;
import org.exoplatform.ide.client.outline.OutlineForm;
import org.exoplatform.ide.client.outline.OutlineTreeGrid;
import org.exoplatform.ide.client.panel.event.ClosePanelEvent;
import org.exoplatform.ide.client.panel.event.OpenPanelEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class DevelopmentModuleEventHandler implements ShowOutlineHandler, ApplicationSettingsReceivedHandler,
EditorActiveFileChangedHandler
{
   
   private Image OUTLINE_TAB_ICON = new Image(IDEImageBundle.INSTANCE.outline());
   
   private Handlers handlers;
   
   private HandlerManager eventBus;

   private ApplicationSettings applicationSettings;
   
   private TextEditor activeTextEditor;
   
   private File activeFile;

   public DevelopmentModuleEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(ShowOutlineEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   /**
    * @see org.exoplatform.ide.client.outline.event.ShowOutlineHandler#onShowOutline(org.exoplatform.ide.client.outline.event.ShowOutlineEvent)
    */
   public void onShowOutline(ShowOutlineEvent event)
   {
      applicationSettings.setValue("outline", new Boolean(event.isShow()), Store.COOKIES);
//      eventBus.fireEvent(new OpenPanelEvent(new VersionContentForm(eventBus, version)));
      if (event.isShow())
      {        
         eventBus.fireEvent(new OpenPanelEvent(new OutlineForm(eventBus, activeTextEditor, activeFile), OUTLINE_TAB_ICON, "Outline"));
      }
      else
      {
         eventBus.fireEvent(new ClosePanelEvent(OutlineForm.ID));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      //check, was outline opened
      //to know, to create new OutlineForm
      boolean wasOutlineOpened = outlineOpened(activeTextEditor, activeFile);
      
      activeTextEditor = event.getEditor();
      activeFile = event.getFile();
      
      boolean openOutline = outlineOpened(activeTextEditor, activeFile);
      
      //if outline was closed, but must be opened, open it
      if (openOutline && !wasOutlineOpened)
      {
         eventBus.fireEvent(new OpenPanelEvent(new OutlineForm(eventBus, activeTextEditor, activeFile), OUTLINE_TAB_ICON, "Outline"));
         return;
      }
      
      //if outline was opened, but must be closed, close it
      if (!openOutline && wasOutlineOpened)
      {
         eventBus.fireEvent(new ClosePanelEvent(OutlineForm.ID));
      }
      
//      if (activeFile == null || activeFile.getContentType() == null)
//      {
//         eventBus.fireEvent(new ClosePanelEvent(OutlineForm.ID));
//         return;
//      }
//      
//      if(!activeTextEditor.canCreateTokenList())
//      {
//         eventBus.fireEvent(new ClosePanelEvent(OutlineForm.ID));
//         return;
//      }
//      
//      if (OutlineTreeGrid.haveOutline(activeFile))
//      {
//         //check is outline panel must be opened (is outline stored in cookies)
//         boolean doOpenOutline =
//            applicationSettings.getValueAsBoolean("outline") == null ? false : applicationSettings.getValueAsBoolean("outline");
//         
//         //if outline panel must be opened in new file, but was closed in previous, open it
//         if (doOpenOutline && !wasOutlineOpened)
//         {
//            eventBus.fireEvent(new OpenPanelEvent(new OutlineForm(eventBus, activeTextEditor), OUTLINE_TAB_ICON, "Outline"));
//            return;
//         }
//         //if outline panel must be closed, but it was opene, close it
//         if (!doOpenOutline && wasOutlineOpened)
//         {
//            eventBus.fireEvent(new ClosePanelEvent(OutlineForm.ID));
//         }
//      }
//      else
//      {
//         eventBus.fireEvent(new ClosePanelEvent(OutlineForm.ID));
//      }
   }
   
   /**
    * Determine the state of outline panel 
    * by text editor (can have outline or not), 
    * by file (is null, does outline support file mime type),
    * by outline value, stored in cookies (do user click Outline button to show panel)
    * 
    * @param textEditor - text editor
    * @param file - file
    * @return is outline must be opened to textEditor and file
    */
   private boolean outlineOpened(TextEditor textEditor, File file)
   {
      boolean storedOutlineState =
         applicationSettings.getValueAsBoolean("outline") == null ? false : applicationSettings.getValueAsBoolean("outline");
      boolean canEditorHasOutline = textEditor != null && textEditor.canCreateTokenList();
      boolean canFileHasOutline = file != null && activeFile.getContentType() != null && OutlineTreeGrid.haveOutline(file);
      
      return storedOutlineState && canEditorHasOutline && canFileHasOutline;
   }

}
