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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class Border extends LayoutPanel
{

   /**
    * Default color of border
    */
   private static final String DEFAULT_BORDER_COLOR = "#A7ABB4";
   
   /**
    * Default size of border
    */
   private static final int DEFAULT_BORDER_SIZE = 1;

   /**
    * Size of border
    */
   private int borderSize = DEFAULT_BORDER_SIZE;
   
   /**
    * Color of border
    */
   private String borderColor = DEFAULT_BORDER_COLOR;
   
   /**
    * 
    */
   public Border()
   {
      getElement().setAttribute("component", "Border");
      DOM.setStyleAttribute(getElement(), "overflow", "hidden");
      getElement().getStyle().setBorderColor(borderColor);
      getElement().getStyle().setBorderWidth(borderSize, Unit.PX);
      getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//      setWidth("100%");
//      setHeight("100%");
   }
//   /**
//    * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
//    */
//   @Override
//   public void onResize()
//   {
//      int width = getOffsetWidth();
//      int height = getOffsetHeight();
//      resize(width, height);
//      
//   }
   
   /**
    * Sets new border's size
    * 
    * @param borderSize new border's size
    */
   public void setBorderSize(int borderSize)
   {
      this.borderSize = borderSize;
      getElement().getStyle().setBorderWidth(borderSize, Unit.PX);
      
   }

   /**
    * Sets new border's color
    * 
    * @param borderColor new border's color
    */
   public void setBorderColor(String borderColor)
   {
      this.borderColor = borderColor;
      getElement().getStyle().setBorderColor(borderColor);
   }

   /**
    * Get border's color.
    * 
    * @return border's color
    */
   public String getBorderColor()
   {
      return borderColor;
   }

//   /**
//    * @see org.exoplatform.gwtframework.ui.client.Resizeable#resize(int, int)
//    */
//   @Override
//   public void resize(int width, int height)
//   {
//      width = width < 0 ? 0 : width;
//      height = height < 0 ? 0 : height;
//      setSize(width + "px", height + "px");
//
//      int innerWidth = width - borderSize - borderSize;
//      int innerHeight = height - borderSize - borderSize;
//      innerWidth = innerWidth < 0 ? 0 : innerWidth;
//      innerHeight = innerHeight < 0 ? 0 : innerHeight;
//
//      getElement().getStyle().setWidth(innerWidth, Unit.PX);
//      getElement().getStyle().setHeight(innerHeight, Unit.PX);
//
//      Widget widget = getWidget();
//      if (widget == null)
//      {
//         return;
//      }
//
//      widget.setSize(innerWidth + "px", innerHeight + "px");
//
//      if (widget instanceof Resizeable)
//      {
//         Resizeable resizeable = (Resizeable)widget;
//         resizeable.resize(innerWidth, innerHeight);
//      }
//      else if (widget instanceof RequiresResize)
//      {
//         ((RequiresResize)widget).onResize();
//         return;
//      }
//      
//   }


   public void setMargin(int margin)
   {
      getElement().getStyle().setMargin(margin, Unit.PX);
   }

}
