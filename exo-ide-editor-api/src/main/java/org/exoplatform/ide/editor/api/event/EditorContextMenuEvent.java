/*
 * Copyright (C) 2012 eXo Platform SAS.
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
 * Event occurs, when user calls context menu in editor.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 26, 2012 5:57:23 PM anya $
 * 
 */
public class EditorContextMenuEvent extends GwtEvent<EditorContextMenuHandler>
{

   /**
    * Type, used to register the event.
    */
   public static final GwtEvent.Type<EditorContextMenuHandler> TYPE = new GwtEvent.Type<EditorContextMenuHandler>();

   /**
    * Coordinates of the context menu.
    */
   private int x, y;

   /**
    * Editor's id.
    */
   private String editorId;

   public EditorContextMenuEvent(int x, int y, String editorId)
   {
      this.editorId = editorId;
      this.x = x;
      this.y = y;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorContextMenuHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(EditorContextMenuHandler handler)
   {
      handler.onEditorContextMenu(this);
   }

   /**
    * @return the x
    */
   public int getX()
   {
      return x;
   }

   /**
    * @return the y
    */
   public int getY()
   {
      return y;
   }

   /**
    * @return the editorId
    */
   public String getEditorId()
   {
      return editorId;
   }
}
