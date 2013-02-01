/**
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
 *
 */

package org.exoplatform.gwtframework.ui.client.wrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Wrapper extends Composite implements RequiresResize
{

   public static final int DEFAULT_PADDING = 5;

   private static WrapperUiBinder uiBinder = GWT.create(WrapperUiBinder.class);

   interface WrapperUiBinder extends UiBinder<Widget, Wrapper>
   {
   }

   interface Style extends CssResource
   {

      String contentCellHighlited();

   }

   @UiField
   Style style;

   @UiField
   SimplePanel contentPanel;

   @UiField
   TableElement wraperTable;

   @UiField
   TableCellElement contentCell;

   private boolean highlited = false;

   private int padding = DEFAULT_PADDING;

   public Wrapper()
   {
      this(DEFAULT_PADDING);
   }

   public Wrapper(int padding)
   {
      initWidget(uiBinder.createAndBindUi(this));
      setPadding(padding);

      DOM.setElementAttribute(getElement(), "component-name", "wrapper");
      DOM.setElementAttribute(contentPanel.getElement(), "component-name", "wrapper-content-panel");
   }

   public void add(Widget w)
   {
      contentPanel.add(w);
   }

   public void setPadding(int padding)
   {
      this.padding = padding;
      contentCell.getStyle().setProperty("borderWidth", padding + "px");
   }

   public boolean isHighlited()
   {
      return highlited;
   }

   public void setHighlited(boolean highlited)
   {
      if (this.highlited != highlited)
      {
         if (highlited)
         {
            contentCell.addClassName(style.contentCellHighlited());
         }
         else
         {
            contentCell.removeClassName(style.contentCellHighlited());
         }
      }

      this.highlited = highlited;
   }

   @Override
   protected void onAttach()
   {
      super.onAttach();
      DOM.setStyleAttribute(getParent().getElement(), "overflow", "hidden");
   }

   @Override
   public void onResize()
   {
//      int w = getOffsetWidth();
//      int h = getOffsetHeight();
//      resize(w, h);
   }

//   @Override
//   public void resize(int width, int height)
//   {
//      int contentCellWidth = width - padding - padding;
//      if (contentCellWidth < 0)
//      {
//         contentCellWidth = 0;
//      }
//
//      int contentCellHeight = height - padding - padding;
//      if (contentCellHeight < 0)
//      {
//         contentCellHeight = 0;
//      }
//
//      contentPanel.setPixelSize(contentCellWidth, contentCellHeight);
//
//      Widget w = contentPanel.getWidget();
//      if (w == null)
//      {
//         return;
//      }
//
//      if (w instanceof RequiresResize)
//      {
//         ((RequiresResize)w).onResize();
//      }
//   }

}
