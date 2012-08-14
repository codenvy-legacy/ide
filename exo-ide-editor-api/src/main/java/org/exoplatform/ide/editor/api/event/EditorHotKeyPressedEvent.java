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
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EditorHotKeyPressedEvent extends GwtEvent<EditorHotKeyPressedHandler>
{

   public static final GwtEvent.Type<EditorHotKeyPressedHandler> TYPE = new GwtEvent.Type<EditorHotKeyPressedHandler>();

   private boolean hotKeyHandled = false;

   private boolean isCtrl;

   private boolean isAlt;

   private boolean isShift;

   private int keyCode;

   public EditorHotKeyPressedEvent(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode)
   {
      this.isCtrl = isCtrl;
      this.isAlt = isAlt;
      this.isShift = isShift;
      this.keyCode = keyCode;
   }

   public boolean isCtrl()
   {
      return isCtrl;
   }

   public boolean isAlt()
   {
      return isAlt;
   }

   public boolean isShift()
   {
      return isShift;
   }

   public int getKeyCode()
   {
      return keyCode;
   }

   public void setHotKeyHandled(boolean hotKeyHandled)
   {
      this.hotKeyHandled = hotKeyHandled;
   }

   public boolean isHotKeyHandled()
   {
      return hotKeyHandled;
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorHotKeyPressedHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(EditorHotKeyPressedHandler handler)
   {
      handler.onEditorHotKeyPressed(this);
   }

}
