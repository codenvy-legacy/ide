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
package org.exoplatform.ide.client.framework.editor.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class EditorFileOpenedEvent extends GwtEvent<EditorFileOpenedHandler>
{

   public static final GwtEvent.Type<EditorFileOpenedHandler> TYPE = new GwtEvent.Type<EditorFileOpenedHandler>();

   private FileModel file;

   private String editorDescription;

   private Map<String, FileModel> openedFiles;

   public EditorFileOpenedEvent(FileModel file, String editorDescription, Map<String, FileModel> openedFiles)
   {
      this.file = file;
      this.editorDescription = editorDescription;
      this.openedFiles = openedFiles;
   }

   public FileModel getFile()
   {
      return file;
   }

   public Map<String, FileModel> getOpenedFiles()
   {
      return openedFiles;
   }

   public String getEditorDescription()
   {
      return editorDescription;
   }

   @Override
   protected void dispatch(EditorFileOpenedHandler handler)
   {
      handler.onEditorFileOpened(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorFileOpenedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
