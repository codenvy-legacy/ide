/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ideall.client.editor.event;

import org.exoplatform.ideall.client.model.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Changed by The eXo Platform SAS        .
 * @version $Id: $
 */

public class FileContentChangedEvent extends GwtEvent<FileContentChangedHandler>
{

   public static GwtEvent.Type<FileContentChangedHandler> TYPE = new GwtEvent.Type<FileContentChangedHandler>();

   private File file;

   private boolean hasUndoChanges;

   private boolean hasRedoChanges;

   /**
    * @param changed item 
    */
   public FileContentChangedEvent(File file, boolean hasUndoChanges, boolean hasRedoChanges)
   {
      this.file = file;
      this.hasUndoChanges = hasUndoChanges;
      this.hasRedoChanges = hasRedoChanges;
   }

   @Override
   protected void dispatch(FileContentChangedHandler handler)
   {
      handler.onFileContentChanged(this);
   }

   @Override
   public GwtEvent.Type<FileContentChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return changed item
    */
   public File getFile()
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
