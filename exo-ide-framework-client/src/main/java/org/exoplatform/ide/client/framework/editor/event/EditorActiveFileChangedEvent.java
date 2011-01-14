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

import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.ide.client.framework.vfs.File;

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

   public static final GwtEvent.Type<EditorActiveFileChangedHandler> TYPE =
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
