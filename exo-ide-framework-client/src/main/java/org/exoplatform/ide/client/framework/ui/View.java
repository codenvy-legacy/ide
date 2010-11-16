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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.ui.event.ActivateViewEvent;
import org.exoplatform.ide.client.framework.ui.event.ActivateViewHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * This class is base pane of all UI components.
 * Use as pane in {@link Tab}. 
 * 
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 3, 2010 $
 *
 */
public class View extends Layout implements ActivateViewHandler
{

   private ArrayList<Canvas> buttons = new ArrayList<Canvas>();

   /**
    * White border style  
    */
   private static final String CLEAR_HIGLIGTH_STYLE = "3px solid #FFFFFF";

   /**
    * Blue border style
    */
   private static final String HIGLIDTH_STYLE = "3px solid #7AADE0";

   /**
    * Id if view
    */
   private final String id;

   private HandlerRegistration mouseDownHandler;

   private Handlers handlers;

   public View(final String id, HandlerManager eventBus)
   {
      this.id = id;
      //      setTitle(id);
      handlers = new Handlers(eventBus);
      handlers.addHandler(ActivateViewEvent.TYPE, this);

      setBorder(CLEAR_HIGLIGTH_STYLE);
      setPadding(0);

      mouseDownHandler = addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            activateView();
         }
      });
   }

   
   /**
    * Activate this view.
    * Use only in sub classes of {@link View} 
    */
   protected void activateView()
   {
      ViewHighlightManager.getInstance().selectView(this);
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      ViewHighlightManager.getInstance().viewClosed(this);
      handlers.removeHandlers();
      super.onDestroy();
   }

  
   /**
    * Set view highlight view.
    * Uses only in {@link ViewHighlightManager}
    */
   public void highlightView()
   {
      setBorder(HIGLIDTH_STYLE);
   }

   
   /**
    * Remove highlight frim view
    * Uses only in {@link ViewHighlightManager}
    */
   public void removeFocus()
   {
      setBorder(CLEAR_HIGLIGTH_STYLE);
   }

   /**
    * @return the id of view
    */
   public String getViewId()
   {
      return id;
   }

   /**
    * @param button
    */
   public void addTabButton(Canvas button)
   {
      buttons.add(button);
   }

   /**
    * @return
    */
   public List<Canvas> getColtrolButtons()
   {
      return buttons;
   }

   /**
    * @see com.smartgwt.client.widgets.Canvas#getTitle()
    */
   @Override
   public String getTitle()
   {
      return id;
   }

   /**
    * Empty method. Override in {@link LockableView}
    */
   public void onOpenTab()
   {
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ActivateViewHandler#onActivateView(org.exoplatform.ide.client.framework.ui.event.ActivateViewEvent)
    */
   public void onActivateView(ActivateViewEvent event)
   {
      if (id.equals(event.getViewId()))
         activateView();
   }

}
