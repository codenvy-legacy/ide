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

package org.exoplatform.ide.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires just after editor has initialized (loaded and displayed).
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorInitializedEvent extends GwtEvent<EditorInitializedHandler>
{

   public static final GwtEvent.Type<EditorInitializedHandler> TYPE = new GwtEvent.Type<EditorInitializedHandler>();

   private String editorId;
   
   public EditorInitializedEvent(String editorId)
   {
      this.editorId = editorId;
   }

   public String getEditorId()
   {
      return editorId;
   }

   @Override
   protected void dispatch(EditorInitializedHandler handler)
   {
      handler.onEditorInitialized(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorInitializedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
