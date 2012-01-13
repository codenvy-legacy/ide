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

/**
 * Event is fired when results from text find in file is received.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class EditorTextFoundEvent extends GwtEvent<EditorTextFoundHandler>
{

   public static final GwtEvent.Type<EditorTextFoundHandler> TYPE = new GwtEvent.Type<EditorTextFoundHandler>();

   private boolean textFound;

   /**
    * @param textFound
    */
   public EditorTextFoundEvent(boolean textFound)
   {
      this.textFound = textFound;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(EditorTextFoundHandler handler)
   {
      handler.onEditorTextFound(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorTextFoundHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the textFound
    */
   public boolean isTextFound()
   {
      return textFound;
   }

}
