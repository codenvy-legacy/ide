/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client.edit.switching;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SwitchingEditorCommandHandler implements GoNextEditorHandler, GoPreviousEditorHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, EditorActiveFileChangedHandler
{

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private FileModel activeFile;

   public SwitchingEditorCommandHandler()
   {
      IDE.getInstance().addControl(new GoNextEditorControl());
      IDE.getInstance().addControl(new GoPreviousEditorControl());

      IDE.addHandler(GoNextEditorEvent.TYPE, this);
      IDE.addHandler(GoPreviousEditorEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   @Override
   public void onGoPreviousEditor(GoPreviousEditorEvent event)
   {
      if (activeFile == null && openedFiles.size() < 2)
      {
         return;
      }

      String[] keys = openedFiles.keySet().toArray(new String[openedFiles.size()]);
      for (int i = 0; i < keys.length; i++)
      {
         if (activeFile.getId().equals(keys[i]) && i > 0)
         {
            String prevFileId = keys[i - 1];
            FileModel file = openedFiles.get(prevFileId);
            IDE.fireEvent(new EditorChangeActiveFileEvent(file));
            return;
         }
      }

   }

   @Override
   public void onGoNextEditor(GoNextEditorEvent event)
   {
      if (activeFile == null || openedFiles.size() < 2)
      {
         return;
      }

      String[] keys = openedFiles.keySet().toArray(new String[openedFiles.size()]);
      for (int i = 0; i < keys.length; i++)
      {
         if (activeFile.getId().equals(keys[i]) && i < keys.length - 1)
         {
            String nextFileId = keys[i + 1];
            FileModel file = openedFiles.get(nextFileId);
            IDE.fireEvent(new EditorChangeActiveFileEvent(file));
            return;
         }
      }
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

}
