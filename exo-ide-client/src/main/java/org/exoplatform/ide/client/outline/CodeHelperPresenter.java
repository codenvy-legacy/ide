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
package org.exoplatform.ide.client.outline;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.event.CloseViewEvent;
import org.exoplatform.ide.client.framework.ui.event.CloseViewHandler;
import org.exoplatform.ide.client.framework.ui.event.OpenViewEvent;
import org.exoplatform.ide.client.framework.ui.event.OpenViewHandler;

import java.util.List;

/**
 * Presenter for CodeHelper panel, that displays in
 * right side of IDE.
 * 
 * Handles events, that open and close panels in tabs.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperPresenter implements OpenViewHandler, CloseViewHandler
{
   interface Display
   {
      boolean isShown();
      
      void show();

      void hide();
      
      void addView(View view, Image tabIcon, String title);
      
      void closePanel(String panelId);
      
      List<String> getViewTypes();
   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   public CodeHelperPresenter(HandlerManager bus)
   {
      eventBus = bus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(OpenViewEvent.TYPE, this);
      handlers.addHandler(CloseViewEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.OpenPanelHandler#onOpenPanel(org.exoplatform.ide.client.panel.event.OpenPanelEvent)
    */
   public void onOpenView(OpenViewEvent event)
   {
      View view = event.getView();
      if (view.getType() != null && display.getViewTypes().contains(view.getType()))
      {
         if (!display.isShown())
         {
            display.show();
         }
         display.addView(event.getView(), event.getView().getImage(), event.getView().getTitle());
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.CloseViewHandler#onCloseViewl(org.exoplatform.ide.client.framework.ui.event.CloseViewEvent)
    */
   public void onCloseView(CloseViewEvent event)
   {
      display.closePanel(event.getViewId());
   }

}
