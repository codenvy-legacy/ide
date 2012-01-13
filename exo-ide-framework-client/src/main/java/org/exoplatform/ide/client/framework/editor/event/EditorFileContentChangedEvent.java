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
 * Changed by The eXo Platform SAS .
 * 
 * @version $Id: $
 */

public class EditorFileContentChangedEvent extends GwtEvent<EditorFileContentChangedHandler>
{

   public static final GwtEvent.Type<EditorFileContentChangedHandler> TYPE =
      new GwtEvent.Type<EditorFileContentChangedHandler>();

   private FileModel file;

   private boolean hasUndoChanges;

   private boolean hasRedoChanges;

   public EditorFileContentChangedEvent(FileModel file, boolean hasUndoChanges, boolean hasRedoChanges)
   {
      this.file = file;
      this.hasUndoChanges = hasUndoChanges;
      this.hasRedoChanges = hasRedoChanges;
   }

   @Override
   protected void dispatch(EditorFileContentChangedHandler handler)
   {
      handler.onEditorFileContentChanged(this);
   }

   @Override
   public GwtEvent.Type<EditorFileContentChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return changed item
    */
   public FileModel getFile()
   {
      return file;
   }

   public boolean hasUndoChanges()
   {
      return hasUndoChanges;
   }

   public boolean hasRedoChanges()
   {
      return hasRedoChanges;
   }

}
