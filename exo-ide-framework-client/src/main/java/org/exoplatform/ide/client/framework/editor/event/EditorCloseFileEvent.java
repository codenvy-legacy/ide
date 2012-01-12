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

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorCloseFileEvent extends GwtEvent<EditorCloseFileHandler>
{

   public static final GwtEvent.Type<EditorCloseFileHandler> TYPE = new GwtEvent.Type<EditorCloseFileHandler>();

   private FileModel file;

   private boolean ignoreChanges = false;

   public EditorCloseFileEvent(FileModel file)
   {
      this.file = file;
   }

   public EditorCloseFileEvent(FileModel file, boolean ignoreChanges)
   {
      this.file = file;
      this.ignoreChanges = ignoreChanges;
   }

   public boolean isIgnoreChanges()
   {
      return ignoreChanges;
   }

   /**
    * Return file will be closed
    * @return
    */
   public FileModel getFile()
   {
      return file;
   }

   @Override
   protected void dispatch(EditorCloseFileHandler handler)
   {
      handler.onEditorCloseFile(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorCloseFileHandler> getAssociatedType()
   {
      return TYPE;
   }

}
