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
package org.exoplatform.ide.client.test;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewManagerPresenter implements ViewOpenedHandler, ViewClosedHandler
{

   private Map<String, ViewEx> views = new HashMap<String, ViewEx>();

   public ViewManagerPresenter(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);

      Button selectWorkspaceButton = new Button("Select Workspace");
      DOM.setStyleAttribute(selectWorkspaceButton.getElement(), "zIndex", "100000");
      Button selectSearchButton = new Button("Select Search");
      DOM.setStyleAttribute(selectSearchButton.getElement(), "zIndex", "100000");

      RootPanel.get().add(selectWorkspaceButton, 400, 100);
      RootPanel.get().add(selectSearchButton, 400, 140);

      selectWorkspaceButton.addClickHandler(selectWorkspaceClickHandler);
      selectSearchButton.addClickHandler(selectSearchClickHandler);
   }

   protected ClickHandler selectWorkspaceClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         ViewEx view = views.get("ideWorkspaceView");
         if (view != null)
         {
            view.setViewVisible();
            view.activate();
         }
      }
   };

   protected ClickHandler selectSearchClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         ViewEx view = views.get("ideSearchView");
         if (view != null)
         {
            view.setViewVisible();
            view.activate();
         }
      }
   };

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      views.remove(event.getView().getId());
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      views.put(event.getView().getId(), event.getView());
   }

}
