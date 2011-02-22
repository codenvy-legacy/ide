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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.module.development.event.ShowOutlineHandler;
import org.exoplatform.ide.client.outline.OutlineForm;
import org.exoplatform.ide.client.outline.OutlineTreeGrid;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class DevelopmentModuleEventHandler implements ShowOutlineHandler, ApplicationSettingsReceivedHandler,
   EditorActiveFileChangedHandler, ViewClosedHandler
{

   private Handlers handlers;

   private HandlerManager eventBus;

   private ApplicationSettings applicationSettings;

   private TextEditor activeTextEditor;

   private File activeFile;

   private boolean isClosedByUser = true;

   private View view;

   private static boolean isNeedRunTimer = true;

   public DevelopmentModuleEventHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      handlers.addHandler(ShowOutlineEvent.TYPE, this);
      handlers.addHandler(ViewClosedEvent.TYPE, this);
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
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
      if (event.isShow())
      {
         View view = new OutlineForm(eventBus, activeTextEditor, activeFile);
         IDE.getInstance().openView(view);
      }
      else
      {
         IDE.getInstance().closeView(OutlineForm.ID);
      }
   }

   Timer t = new Timer()
   {

      @Override
      public void run()
      {
         view.blur();
         view.focus();
      }
   };

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
         view = new OutlineForm(eventBus, activeTextEditor, activeFile);
         IDE.getInstance().openView(view);
         if (isNeedRunTimer)
         {
            //run timer on load IDE
            isNeedRunTimer = false;
            t.schedule(750);
         }
         return;
      }

      //if outline was opened, but must be closed, close it
      if (!openOutline && wasOutlineOpened)
      {
         isClosedByUser = false;
         IDE.getInstance().closeView(OutlineForm.ID);
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
         applicationSettings.getValueAsBoolean("outline") == null ? false : applicationSettings
            .getValueAsBoolean("outline");
      boolean canEditorHasOutline = textEditor != null && textEditor.canCreateTokenList();
      boolean canFileHasOutline =
         file != null && activeFile.getContentType() != null && OutlineTreeGrid.haveOutline(file);

      return storedOutlineState && canEditorHasOutline && canFileHasOutline;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent)
    */
   public void onViewClosed(ViewClosedEvent event)
   {
      if (OutlineForm.ID.equals(event.getViewId()) && isClosedByUser)
      {
         applicationSettings.setValue("outline", new Boolean(false), Store.COOKIES);
         eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
      }
      isClosedByUser = true;
   }

}
