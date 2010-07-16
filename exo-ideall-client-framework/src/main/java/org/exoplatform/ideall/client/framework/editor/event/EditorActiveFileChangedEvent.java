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

package org.exoplatform.ideall.client.framework.editor.event;

import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.ideall.client.module.vfs.api.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 * 
 * Fired when changed active file in editor 
 * 
 */
public class EditorActiveFileChangedEvent extends GwtEvent<EditorActiveFileChangedHandler>
{

   public static GwtEvent.Type<EditorActiveFileChangedHandler> TYPE =
      new GwtEvent.Type<EditorActiveFileChangedHandler>();

   private File file;

   private TextEditor editor;
   
   public EditorActiveFileChangedEvent(File file, TextEditor editor)
   {
      this.file = file;
      this.editor = editor;
   }

   @Override
   protected void dispatch(EditorActiveFileChangedHandler handler)
   {
      handler.onEditorActiveFileChanged(this);
   }

   @Override
   public GwtEvent.Type<EditorActiveFileChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public File getFile()
   {
      return file;
   }

   /**
    * @return the editor
    */
   public TextEditor getEditor()
   {
      return editor;
   }

}
