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
package org.exoplatform.ide.client.framework.ui;

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

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 8, 2010 $
 *
 */
public class LockableView extends View implements ResizedHandler, LockIFrameElementsHandler,
UnlockIFrameElementsHandler
{

   private HandlerManager eventBus;
   
   protected Handlers lockingHandlers;

   private Canvas lockCanvas;

   private boolean lockingEnabled;
   
   /**
    * @param id
    */
   public LockableView(String id, HandlerManager eventBus, boolean lockingEnabled)
   {
      super(id, eventBus);
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
   public void onCloseTab()
   {
      lockingHandlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.View#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      lockingHandlers.removeHandlers();
      super.onDestroy();
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
      lockCanvas.setOpacity(0);
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
