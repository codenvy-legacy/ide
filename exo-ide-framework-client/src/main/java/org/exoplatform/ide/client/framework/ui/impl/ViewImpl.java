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
package org.exoplatform.ide.client.framework.ui.impl;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;
import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasSetViewVisibleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewImpl extends FlowPanel implements ViewEx, IsView, HasChangeViewTitleHandler,
   HasChangeViewIconHandler, HasSetViewVisibleHandler
{

   protected class ViewScrollPanel extends ScrollPanel
   {
      public ViewScrollPanel()
      {
         DOM.setStyleAttribute(getContainerElement(), "position", "absolute");
         DOM.setStyleAttribute(getContainerElement(), "left", "0px");
         DOM.setStyleAttribute(getContainerElement(), "top", "0px");
         DOM.setStyleAttribute(getContainerElement(), "width", "100%");
         DOM.setStyleAttribute(getContainerElement(), "height", "100%");
         setSize("100%", "100%");
      }
   }

   private boolean activated = false;

   private boolean canResize = true;

   private List<ChangeViewIconHandler> changeViewIconHandlers = new ArrayList<ChangeViewIconHandler>();

   private List<ChangeViewTitleHandler> changeViewTitleHandlers = new ArrayList<ChangeViewTitleHandler>();

   protected int defaultHeight = 200;

   protected int defaultWidth = 300;

   private boolean hasCloseButton = true;

   private Image icon;

   private String id;

   private ViewScrollPanel scrollPanel;

   private List<SetViewVisibleHandler> setViewVisibleHandlers = new ArrayList<SetViewVisibleHandler>();

   private String tiltle;

   private String type;

   private Wrapper wrapper;

   public ViewImpl(String id, String type, String title)
   {
      this(id, type, title, null);
   }

   public ViewImpl(String id, String type, String title, Image icon)
   {
      this(id, type, title, icon, 300, 200);
   }

   public ViewImpl(String id, String type, String title, Image icon, int defaultWidth, int defaultHeight)
   {
      this.id = id;
      this.type = type;
      this.tiltle = title;
      this.icon = icon;
      this.defaultWidth = defaultWidth;
      this.defaultHeight = defaultHeight;
      getElement().setAttribute("view-id", id);

      wrapper = new Wrapper(3);
      wrapper.setSize("100%", "100%");
      super.add((Widget)wrapper);

      sinkEvents(Event.ONMOUSEDOWN);
   }

   @Override
   public void activate()
   {
      if (isViewVisible())
      {
         setViewVisible();
      }

      ViewHighlightManager.getInstance().selectView(this);
   }

   @Override
   public final void add(Widget w)
   {
      add(w, false);
   }

   public final void add(Widget w, boolean contentScrollable)
   {
      if (contentScrollable)
      {
         if (scrollPanel == null)
         {
            scrollPanel = new ViewScrollPanel();
            DOM.setStyleAttribute(scrollPanel.getElement(), "zIndex", "0");
            wrapper.add(scrollPanel);
         }
         scrollPanel.add(w);
         w.setSize("100%", "100%");
      }
      else
      {
         wrapper.add(w);
      }
   }

   @Override
   public HandlerRegistration addChangeViewIconHandler(ChangeViewIconHandler changeViewIconHandler)
   {
      changeViewIconHandlers.add(changeViewIconHandler);
      return new ListBasedHandlerRegistration(changeViewIconHandlers, changeViewIconHandler);
   }

   @Override
   public HandlerRegistration addChangeViewTitleHandler(ChangeViewTitleHandler changeViewTitleHandler)
   {
      changeViewTitleHandlers.add(changeViewTitleHandler);
      return new ListBasedHandlerRegistration(changeViewTitleHandlers, changeViewTitleHandler);
   }

   @Override
   public HandlerRegistration addSetViewVisibleHandler(SetViewVisibleHandler setViewVisibleHandler)
   {
      setViewVisibleHandlers.add(setViewVisibleHandler);
      return new ListBasedHandlerRegistration(setViewVisibleHandlers, setViewVisibleHandler);
   }

   @Override
   public ViewEx asView()
   {
      return this;
   }

   @Override
   public boolean canResize()
   {
      return canResize;
   }

   @Override
   public int getDefaultHeight()
   {
      return defaultHeight;
   }

   @Override
   public int getDefaultWidth()
   {
      return defaultWidth;
   }

   @Override
   public Image getIcon()
   {
      return icon;
   }

   @Override
   public String getId()
   {
      return id;
   }

   public ScrollPanel getScrollPanel()
   {
      return scrollPanel;
   }

   @Override
   public String getTitle()
   {
      return tiltle;
   }

   @Override
   public String getType()
   {
      return type;
   }

   @Override
   public boolean hasCloseButton()
   {
      return hasCloseButton;
   }

   @Override
   public boolean isViewVisible()
   {
      return isVisible();
   }

   @Override
   public void onBrowserEvent(Event event)
   {
      switch (DOM.eventGetType(event))
      {
         case Event.ONMOUSEDOWN :
            if (event.getButton() != Event.BUTTON_LEFT)
            {
               return;
            }

            onMouseDown();
            break;
      }
   }

   protected void onMouseDown()
   {
      ViewHighlightManager.getInstance().selectView(this);
   }

   public void setActivated(boolean activated)
   {
      this.activated = activated;
      wrapper.setHighlited(activated);
   }

   public void setHasCloseButton(boolean hasCloseButton)
   {
      this.hasCloseButton = hasCloseButton;
   }

   @Override
   public void setIcon(Image icon)
   {
      this.icon = icon;

      ChangeViewIconEvent changeViewIconEvent = new ChangeViewIconEvent(getId(), icon);
      for (ChangeViewIconHandler changeViewIconHandler : changeViewIconHandlers)
      {
         changeViewIconHandler.onChangeViewIcon(changeViewIconEvent);
      }
   }

   @Override
   public void setTitle(String title)
   {
      this.tiltle = title;
      
      ChangeViewTitleEvent changeViewTitleEvent = new ChangeViewTitleEvent(getId(), title);
      for (ChangeViewTitleHandler changeViewTitleHandler : changeViewTitleHandlers)
      {
         changeViewTitleHandler.onChangeViewTitle(changeViewTitleEvent);
      }
   }

   @Override
   public void setViewVisible()
   {
      SetViewVisibleEvent event = new SetViewVisibleEvent(getId());

      for (SetViewVisibleHandler setViewVisibleHandler : setViewVisibleHandlers)
      {
         setViewVisibleHandler.onSetViewVisible(event);
      }
   }

}
