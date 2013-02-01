/**
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
 *
 */

package org.exoplatform.gwtframework.ui.client.menu;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * This Lock Layer for Popup Menu uses as root for for Popup Menus and uses for closing all visible popups when user clicked outside one of them.  
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuLockLayer extends AbsolutePanel
{

   /**
    *  Lock Layer uses for locking of screen. Uses for hiding popups.
    */
   private class LockLayer extends AbsolutePanel
   {

      public LockLayer()
      {
         sinkEvents(Event.ONMOUSEDOWN);
      }

      @Override
      public void onBrowserEvent(Event event)
      {
         switch (DOM.eventGetType(event))
         {
            case Event.ONMOUSEDOWN :
               close();
               break;
         }
      }

   }

   /**
    * Callback which is uses for closing Popup menu.
    */
   private CloseMenuHandler closeMenuCallback;

   private int topOffset = 20;

   public MenuLockLayer()
   {

   }

   /**
    * Create Menu Lock Layer.
    * 
    * @param closeMenuCallback - callback which is uses for 
    */
   public MenuLockLayer(CloseMenuHandler closeMenuCallback)
   {
      this(closeMenuCallback, 0);
   }

   public MenuLockLayer(CloseMenuHandler closeMenuCallback, int topOffset)
   {
      this.closeMenuCallback = closeMenuCallback;
      this.topOffset = topOffset;

      RootPanel.get().add(this, 0, topOffset);
      int width = Window.getClientWidth();
      int height = Window.getClientHeight() - topOffset;
      setWidth("" + width + "px");
      setHeight("" + height + "px");
      DOM.setElementAttribute(getElement(), "id", "menu-lock-layer-id");
      DOM.setStyleAttribute(getElement(), "zIndex", "" + (Integer.MAX_VALUE - 5));

      AbsolutePanel blockMouseEventsPanel = new LockLayer();
      blockMouseEventsPanel.setStyleName("exo-lockLayer");
      int lockWidth = Window.getClientWidth();
      int lockHeight = Window.getClientHeight() - topOffset;
      blockMouseEventsPanel.setWidth("" + lockWidth + "px");
      blockMouseEventsPanel.setHeight("" + lockHeight + "px");
      add(blockMouseEventsPanel, 0, 0);
   }

   public void close()
   {
      removeFromParent();
      if (closeMenuCallback != null)
      {
         closeMenuCallback.onCloseMenu();
      }
   }

   public int getTopOffset()
   {
      return topOffset;
   }

}
