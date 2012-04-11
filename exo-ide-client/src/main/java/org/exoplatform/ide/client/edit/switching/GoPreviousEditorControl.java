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

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GoPreviousEditorControl extends SimpleControl implements IDEControl, EditorFileOpenedHandler,
   EditorFileClosedHandler, EditorActiveFileChangedHandler
{

   public static final String ID = "Window/Navigation/Previous Editor";

   public static final String TITLE = "Previous Editor";

   public static final String PROMPT = "Switch to Previous Editor";

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   private File activeFile;

   public GoPreviousEditorControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.back(), IDEImageBundle.INSTANCE.backDisabled());
      setEvent(new GoPreviousEditorEvent());
      setHotKey("Ctrl+Shift+PageUp");
   }

   @Override
   public void initialize()
   {
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      setVisible(true);
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      update();
   }

   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      update();
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      update();
   }

   private void update()
   {
      if (activeFile == null)
      {
         setEnabled(false);
         return;
      }

      String[] keys = openedFiles.keySet().toArray(new String[openedFiles.size()]);
      if (keys.length == 1)
      {
         setEnabled(false);
         return;
      }

      int pos = 0;
      for (String key : keys)
      {
         if (activeFile.getId().equals(key) && pos > 0)
         {
            setEnabled(true);
            return;
         }

         pos++;
      }

      setEnabled(false);
   }

}
