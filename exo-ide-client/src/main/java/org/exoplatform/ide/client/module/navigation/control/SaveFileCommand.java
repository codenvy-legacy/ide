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

import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesSavedHandler;
import org.exoplatform.ide.client.operation.properties.event.FilePropertiesChangedEvent;
import org.exoplatform.ide.client.operation.properties.event.FilePropertiesChangedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileCommand extends SimpleControl implements EditorActiveFileChangedHandler,
   ItemPropertiesSavedHandler, EditorFileContentChangedHandler, FilePropertiesChangedHandler, FileContentSavedHandler,
   EntryPointChangedHandler
{

   public static final String ID = "File/Save";

   public static final String TITLE = "Save";

   private File activeFile;

   public SaveFileCommand(HandlerManager eventBus)
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.save(), IDEImageBundle.INSTANCE.saveDisabled());
      setEvent(new SaveFileEvent());
      setIgnoreDisable(true);

      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ItemPropertiesSavedEvent.TYPE, this);
      eventBus.addHandler(EditorFileContentChangedEvent.TYPE, this);
      eventBus.addHandler(FilePropertiesChangedEvent.TYPE, this);
      eventBus.addHandler(FileContentSavedEvent.TYPE, this);
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
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

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (activeFile == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (activeFile.isNewFile())
      {
         setEnabled(false);
      }
      else
      {
         if (activeFile.isContentChanged() || activeFile.isPropertiesChanged())
         {
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
         }
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      if (!(event.getItem() instanceof File))
      {
         return;
      }

      if ((File)event.getItem() != activeFile)
      {
         return;
      }

      File file = (File)event.getItem();

      if (file.isContentChanged())
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      if (event.getFile().isNewFile())
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   public void onFilePropertiesChanged(FilePropertiesChangedEvent event)
   {
      if (event.getFile().isNewFile())
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (event.getFile() != activeFile)
      {
         return;
      }

      if (event.getFile().isPropertiesChanged())
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

}
