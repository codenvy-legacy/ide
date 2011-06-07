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
package org.exoplatform.ide.client.editor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MinMaxControlButton extends Image implements com.google.gwt.event.dom.client.ClickHandler
{
   
   private static final String MAXIMIZE = IDE.IDE_LOCALIZATION_CONSTANT.minMaxMaximizeControl();
   
   private static final String RESTORE = IDE.IDE_LOCALIZATION_CONSTANT.minMaxRestoreControl();

   private HandlerManager eventBus;

   private GwtEvent<?> maximizeEvent;

   private GwtEvent<?> minimizeEvent;

   private boolean maximize;

   public MinMaxControlButton(HandlerManager eventBus, boolean maximize, GwtEvent<?> maximizeEvent,
      GwtEvent<?> minimizeEvent)
   {
      this.eventBus = eventBus;
      this.maximize = maximize;

      this.maximizeEvent = maximizeEvent;
      this.minimizeEvent = minimizeEvent;

      setWidth(20+"px");
      setHeight(18+"px");

      updateVisualState();
      addClickHandler(this);
   }
   
   public void setMaximize(boolean maximize)
   {
      this.maximize = maximize;
      updateVisualState();
   }

   private void updateVisualState()
   {
      if (maximize)
      {
         setUrl(Images.ControlButtons.MAXIMIZE);
         setTitle(MAXIMIZE);
      }
      else
      {
         setUrl(Images.ControlButtons.RESTORE);
         setTitle(RESTORE);
      }
   }

   public void onClick(ClickEvent event)
   {
      if (maximize)
      {
         maximize = false;
         updateVisualState();
         eventBus.fireEvent(maximizeEvent);
      }
      else
      {
         maximize = true;
         updateVisualState();
         eventBus.fireEvent(minimizeEvent);
      }
   }
}
