/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.module.navigation.control;

import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.module.navigation.event.SaveAllFilesEvent;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommand extends SimpleControl implements EditorFileContentChangedHandler,
   FileContentSavedHandler, EditorActiveFileChangedHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
   EntryPointChangedHandler
{

   public static final String ID = "File/Save All";

   public static final String TITLE = "Save All";

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   public SaveAllFilesCommand(HandlerManager eventBus)
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(IDEImageBundle.INSTANCE.saveAll(), IDEImageBundle.INSTANCE.saveAllDisabled());
      setEvent(new SaveAllFilesEvent());

      eventBus.addHandler(EditorFileContentChangedEvent.TYPE, this);
      eventBus.addHandler(FileContentSavedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
   }

   private void checkItemEnabling()
   {
      boolean enable = false;
      for (File file : openedFiles.values())
      {
         if (!file.isNewFile() && file.isContentChanged())
         {
            enable = true;
            break;
         }
      }

      setEnabled(enable);
   }

   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      checkItemEnabling();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      checkItemEnabling();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      checkItemEnabling();
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      if (event.getEntryPoint() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }
   }

}
