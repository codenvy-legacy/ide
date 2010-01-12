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
package org.exoplatform.ideall.client.editor.codemirror;

import org.exoplatform.gwt.commons.component.event.LockIFrameElementsEvent;
import org.exoplatform.gwt.commons.component.event.LockIFrameElementsHandler;
import org.exoplatform.gwt.commons.component.event.UnlockIFrameElementsEvent;
import org.exoplatform.gwt.commons.component.event.UnlockIFrameElementsHandler;
import org.exoplatform.ideall.client.Handlers;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class LockableCanvas extends Canvas implements ResizedHandler, LockIFrameElementsHandler, UnlockIFrameElementsHandler
{

   protected HandlerManager eventBus;

   protected Handlers handlers;

   private Canvas lockCanvas;

   public LockableCanvas(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      addResizedHandler(this);

      handlers.addHandler(LockIFrameElementsEvent.TYPE, this);
      handlers.addHandler(UnlockIFrameElementsEvent.TYPE, this);

      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.cancel();
         }
      });
   }

   @Override
   public void destroy()
   {
      handlers.removeHandlers();
      super.destroy();
   }

   public void onResized(ResizedEvent event)
   {
      unlock();
      onResized();
   }

   /**
    * Owerride this method to finalize resizing cycle.
    */
   protected abstract void onResized();
   
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

   public void onUnlockIFrameElements(UnlockIFrameElementsEvent event)
   {
      unlock();
   }

}
