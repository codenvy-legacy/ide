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
package org.exoplatform.ide.client.framework.ui;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.event.LockIFrameElementsEvent;
import org.exoplatform.gwtframework.ui.client.event.LockIFrameElementsHandler;
import org.exoplatform.gwtframework.ui.client.event.UnlockIFrameElementsEvent;
import org.exoplatform.gwtframework.ui.client.event.UnlockIFrameElementsHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class TabPanel extends Layout implements ResizedHandler, LockIFrameElementsHandler,
   UnlockIFrameElementsHandler
{

   private ArrayList<Canvas> buttons = new ArrayList<Canvas>();

   private boolean lockingEnabled;

   protected HandlerManager eventBus;

   protected Handlers lockingHandlers;

   private Canvas lockCanvas;

   public TabPanel(HandlerManager eventBus, boolean lockingEnabled)
   {
      this.eventBus = eventBus;
      this.lockingEnabled = lockingEnabled;

      lockingHandlers = new Handlers(eventBus);

      if (lockingEnabled)
      {
         initLocking();
      }
   }

   @Override
   public void destroy()
   {
      super.destroy();
   }

   private void initLocking()
   {
      addResizedHandler(this);
      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.cancel();
         }
      });
   }

   public boolean isLockingEnabled()
   {
      return lockingEnabled;
   }

   public void addTabButton(Canvas button)
   {
      buttons.add(button);
   }

   public List<Canvas> getColtrolButtons()
   {
      return buttons;
   }

   public abstract String getTitle();

   public abstract String getId();

   public void onCloseTab()
   {
      lockingHandlers.removeHandlers();
   }

   public void onOpenTab()
   {
      if (lockingEnabled)
      {
         lockingHandlers.addHandler(LockIFrameElementsEvent.TYPE, this);
         lockingHandlers.addHandler(UnlockIFrameElementsEvent.TYPE, this);
      }
   }

   public void onResized(ResizedEvent event)
   {
      unlock();
      onResized();
   }

   /**
    * Owerride this method to finalize resizing cycle.
    */
   protected void onResized()
   {

   }

   private void lock()
   {
      if (!isVisible())
      {
         return;
      }

      if (lockCanvas != null)
      {
         return;
      }

      lockCanvas = new Canvas();
      lockCanvas.setBackgroundColor("#3344FF");
      lockCanvas.setOpacity(50);
      lockCanvas.setWidth100();
      lockCanvas.setHeight100();
      addChild(lockCanvas);
   }

   private void unlock()
   {
      if (lockCanvas != null)
      {
         lockCanvas.destroy();
         lockCanvas = null;
      }
   }

   public void onLockIFrameElements(LockIFrameElementsEvent event)
   {
      lock();
   }

   public void onUnlockIFrameElements(UnlockIFrameElementsEvent event)
   {
      unlock();
   }

}
