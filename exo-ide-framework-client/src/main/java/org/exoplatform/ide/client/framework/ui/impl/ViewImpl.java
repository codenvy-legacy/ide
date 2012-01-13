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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.BeforeViewLoseActivityEvent;
import org.exoplatform.ide.client.framework.ui.api.event.BeforeViewLoseActivityHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewLostActivityEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewLostActivityHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasSetViewVisibleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewImpl extends LayoutPanel implements View, IsView, HasChangeViewTitleHandler, HasChangeViewIconHandler,
   HasSetViewVisibleHandler
{

   /**
    * Is this view activated
    */
   private boolean activated = false;

   /**
    * Is this view can be resized
    */
   private boolean canResize = true;

   /**
    * View's default height
    */
   protected int defaultHeight = 200;

   /**
    * View's default width
    */
   protected int defaultWidth = 300;

   /**
    * Is this view has close button ( can be closed )
    */
   private boolean canBeClosed = true;

   private boolean closeOnEscape = false;

   /**
    * View's icon
    */
   private Image icon;

   /**
    * View's ID
    */
   private String id;

   /**
    * Title of this view.
    */
   private String tiltle;

   /**
    * Type of this view.
    */
   private String type;

   private Border viewBorder;

   /**
    * User defined content which will be displayed in this view.
    */
   private Widget viewWidget;

   /**
    * Creates a new instance of this View implementation with specified parameters.
    * 
    * @param id id of this view
    * @param type type of this view
    * @param title title of this view
    */
   public ViewImpl(String id, String type, String title)
   {
      this(id, type, title, null);
   }

   /**
    * Creates a new instance of this View implementation with specified parameters.
    * 
    * @param id id of this view
    * @param type type of this view
    * @param title title of this view
    * @param icon icon of this view
    */
   public ViewImpl(String id, String type, String title, Image icon)
   {
      this(id, type, title, icon, 300, 200);
   }

   /**
    * Creates a new instance of this View implementation with specified parameters.
    * 
    * @param id d of this view
    * @param type type of this view
    * @param title title of this view
    * @param icon icon of this view
    * @param defaultWidth view's default width
    * @param defaultHeight view's default height
    */
   public ViewImpl(String id, String type, String title, Image icon, int defaultWidth, int defaultHeight)
   {
      this(id, type, title, icon, defaultWidth, defaultHeight, true);
   }

   /**
    * Creates a new instance of this View implementation with specified parameters.
    * 
    * @param id id of this view
    * @param type type of this view
    * @param title title of this view
    * @param icon icon of this view
    * @param defaultWidth view's default width
    * @param defaultHeight view's default height
    * @param canResize is this view resizeable
    */
   public ViewImpl(String id, String type, String title, Image icon, int defaultWidth, int defaultHeight,
      boolean canResize)
   {
      this.id = id;
      this.type = type;
      this.tiltle = title;
      this.icon = icon;
      this.defaultWidth = defaultWidth;
      this.defaultHeight = defaultHeight;
      this.canResize = canResize;

      getElement().setAttribute("view-id", id);
      getElement().setAttribute("is-active", "false");
      getElement().getStyle().setOverflow(Overflow.HIDDEN);
      getElement().getStyle().setPosition(Position.RELATIVE);
      if (canResize)
         getElement().getStyle().setHeight(100, Unit.PCT);
      else
         getElement().getStyle().setHeight(defaultHeight, Unit.PX);

      viewBorder = GWT.create(Border.class);
      viewBorder.setBorderSize(3);
      viewBorder.getElement().getStyle().setMargin(1, Unit.PX);
      super.add(viewBorder);
      setWidgetTopHeight(viewBorder, 0, Unit.PX, 100, Unit.PCT);
      sinkEvents(Event.ONMOUSEDOWN);
   }

   /**
    * Set's this view activate.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#activate()
    */
   @Override
   public void activate()
   {
      if (!isViewVisible())
      {
         setViewVisible();
      }

      ViewHighlightManager.getInstance().activateView(this);
   }

   /**
    * Add user defined content into this view.
    * 
    * @param w user defined widget
    */
   @Override
   public final void add(Widget w)
   {
      viewWidget = w;
      viewBorder.add(viewWidget);
      // super.add();
      viewBorder.setWidgetTopHeight(w, 0, Unit.PX, 100, Unit.PCT);
      // setWidgetBottomHeight(w, 100, Unit.PCT, 100, Unit.PCT);
   }

   /**
    * Adds ChangeViewIconHandler to this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewIconHandler#addChangeViewIconHandler(org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconHandler)
    */
   @Override
   public HandlerRegistration addChangeViewIconHandler(ChangeViewIconHandler changeViewIconHandler)
   {
      return addHandler(changeViewIconHandler, ChangeViewIconEvent.TYPE);
   }

   /**
    * Adds ChangeViewTitleHandler to this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewTitleHandler#addChangeViewTitleHandler(org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleHandler)
    */
   @Override
   public HandlerRegistration addChangeViewTitleHandler(ChangeViewTitleHandler changeViewTitleHandler)
   {
      return addHandler(changeViewTitleHandler, ChangeViewTitleEvent.TYPE);
   }

   /**
    * Adds SetViewVisibleHandler to this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.impl.event.HasSetViewVisibleHandler#addSetViewVisibleHandler(org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleHandler)
    */
   @Override
   public HandlerRegistration addSetViewVisibleHandler(SetViewVisibleHandler setViewVisibleHandler)
   {
      return addHandler(setViewVisibleHandler, SetViewVisibleEvent.TYPE);
   }

   /**
    * Returns this view as {@link View}.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.IsView#asView()
    */
   @Override
   public View asView()
   {
      return this;
   }

   /**
    * Determines whether this view can be resized.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#canResize()
    */
   @Override
   public boolean canResize()
   {
      return canResize;
   }

   /**
    * Get default height of this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#getDefaultHeight()
    */
   @Override
   public int getDefaultHeight()
   {
      return defaultHeight;
   }

   /**
    * Get default width of this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#getDefaultWidth()
    */
   @Override
   public int getDefaultWidth()
   {
      return defaultWidth;
   }

   /**
    * Get icon of this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#getIcon()
    */
   @Override
   public Image getIcon()
   {
      return icon;
   }

   /**
    * Get ID of this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#getId()
    */
   @Override
   public String getId()
   {
      return id;
   }

   /**
    * Get title of this view.
    * 
    * @see com.google.gwt.user.client.ui.UIObject#getTitle()
    */
   @Override
   public String getTitle()
   {
      return tiltle;
   }

   /**
    * Get type of this view.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#getType()
    */
   @Override
   public String getType()
   {
      return type;
   }

   /**
    * Determines whether this view can be closed.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#canBeClosed()
    */
   @Override
   public boolean canBeClosed()
   {
      return canBeClosed;
   }

   /**
    * Determined whether this view is visible.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#isViewVisible()
    */
   @Override
   public boolean isViewVisible()
   {
      return isVisible();
   }

   /**
    * Handle of browser events.
    * 
    * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user.client.Event)
    */
   @Override
   public void onBrowserEvent(Event event)
   {
      switch (DOM.eventGetType(event))
      {
         case Event.ONMOUSEDOWN :
            if (event.getButton() == Event.BUTTON_LEFT)
            {
               onMouseDown();
            }
            break;
      }

      super.onBrowserEvent(event);
   }

   /**
    * Mouse down handler.
    */
   protected void onMouseDown()
   {
      ViewHighlightManager.getInstance().activateView(this);
   }

   /**
    * Sets this view activated.
    * 
    * @param activated
    */
   public void setActivated(boolean activated)
   {
      this.activated = activated;
      viewBorder.setBorderColor(activated ? "#B6CCE8" : "transparent");
      // getElement().getStyle().setBorderColor(activated ? "#B6CCE8" : "transparent");

      /*
       * Attribute for Selenium Tests
       */
      getElement().setAttribute("is-active", "" + activated);
   }

   /**
    * Sets this view can be closed.
    * 
    * @param canBeClosed <b>true</b> makes view closeable, <
    */
   public void setCanBeClosed(boolean canBeClosed)
   {
      this.canBeClosed = canBeClosed;
   }

   /**
    * Sets new icon.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#setIcon(com.google.gwt.user.client.ui.Image)
    */
   @Override
   public void setIcon(Image icon)
   {
      this.icon = icon;

      ChangeViewIconEvent changeViewIconEvent = new ChangeViewIconEvent(getId(), icon);
      fireEvent(changeViewIconEvent);
   }

   /**
    * Sets new title.
    * 
    * @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String)
    */
   @Override
   public void setTitle(String title)
   {
      this.tiltle = title;

      ChangeViewTitleEvent changeViewTitleEvent = new ChangeViewTitleEvent(getId(), title);
      fireEvent(changeViewTitleEvent);
   }

   /**
    * Sets this view is visible.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.View#setViewVisible()
    */
   @Override
   public void setViewVisible()
   {
      SetViewVisibleEvent event = new SetViewVisibleEvent(getId());
      fireEvent(event);
   }

   @Override
   public boolean isActive()
   {
      return activated;
   }

   @Override
   public HandlerRegistration addBeforeViewLoseActivityHandler(
      BeforeViewLoseActivityHandler beforeViewLoseActivityHandler)
   {
      return addHandler(beforeViewLoseActivityHandler, BeforeViewLoseActivityEvent.TYPE);
   }

   @Override
   public HandlerRegistration addViewLoseActivityHandler(ViewLostActivityHandler viewLoseActivityHandler)
   {
      return addHandler(viewLoseActivityHandler, ViewLostActivityEvent.TYPE);
   }

   @Override
   public String toString()
   {
      return "ViewImpl [ ID: " + id + " ]";
   }

   protected void setCloseOnEscape(boolean closeOnEscape)
   {
      this.closeOnEscape = closeOnEscape;
   }

   @Override
   public boolean closeOnEscape()
   {
      return closeOnEscape;
   }

}
