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

package org.exoplatform.ide.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires just after some key or mouse event have been happened in editor.
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorCursorActivityEvent extends GwtEvent<EditorCursorActivityHandler>
{

   public static final GwtEvent.Type<EditorCursorActivityHandler> TYPE =
      new GwtEvent.Type<EditorCursorActivityHandler>();

   private String editorId;

   private int row = 0;

   private int column = 0;

   public EditorCursorActivityEvent(String editorId)
   {
      this.editorId = editorId;
   }

   public EditorCursorActivityEvent(String editorId, int row, int column)
   {
      this.editorId = editorId;
      this.row = row;
      this.column = column;
   }

   public String getEditorId()
   {
      return editorId;
   }

   public int getColumn()
   {
      return column;
   }

   public int getRow()
   {
      return row;
   }

   @Override
   protected void dispatch(EditorCursorActivityHandler handler)
   {
      handler.onEditorCursorActivity(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorCursorActivityHandler> getAssociatedType()
   {
      return TYPE;
   }

}
