/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.create;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 20, 2012 2:44:29 PM anya $
 * 
 */
public class ProjectTypeTreeViewModel implements TreeViewModel
{
   public static class ProjectTypeCell extends AbstractCell<Object>
   {
      protected Widget createItemWidget(Object object)
      {
         FlowPanel flowPanel = new FlowPanel();
         Element span = DOM.createSpan();
         if (object instanceof LanguageItem)
         {
            span.setInnerHTML(((LanguageItem)object).getName().value());

            ImageResource imageResource = ProjectResolver.getImageForLanguage(((LanguageItem)object).getName());
            if (imageResource != null)
            {
               Image image = new Image(imageResource);
               DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
               DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
               flowPanel.add(image);
            }
         }
         else if (object instanceof ProjectType)
         {
            span.setInnerHTML(((ProjectType)object).value());
            Image image = new Image(ProjectResolver.getImageForProject((ProjectType)object));
            DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
            DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
            flowPanel.add(image);
         }
         flowPanel.getElement().appendChild(span);
         return flowPanel;
      }

      /**
       * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
       *      com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb)
      {
         if (value instanceof LanguageItem || value instanceof ProjectType)
         {
            sb.appendHtmlConstant((createItemWidget(value)).getElement().getInnerHTML());
         }
      }
   }

   private SingleSelectionModel<Object> selectionModel;

   private ListDataProvider<Object> dataProvider = new ListDataProvider<Object>();

   public ProjectTypeTreeViewModel(SingleSelectionModel<Object> selectionModel)
   {
      this.selectionModel = selectionModel;
   }

   /**
    * @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object)
    */
   @Override
   public <T> NodeInfo<?> getNodeInfo(T value)
   {
      if (value == null)
      {
         return new DefaultNodeInfo<Object>(dataProvider, new ProjectTypeCell(), selectionModel, null);
      }
      else
      {
         if (value instanceof LanguageItem)
         {
            List<Object> list = new ArrayList<Object>();
            for (ProjectType projectType : ((LanguageItem)value).getProjectTypes())
            {
               list.add(projectType);
            }
            return new DefaultNodeInfo<Object>(new ListDataProvider<Object>(list), new ProjectTypeCell(),
               selectionModel, null);
         }
         return new DefaultNodeInfo<Object>(dataProvider, new ProjectTypeCell(), selectionModel, null);
      }
   }

   /**
    * @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object)
    */
   @Override
   public boolean isLeaf(Object value)
   {
      if (value == null)
         return false;

      if (value instanceof LanguageItem)
      {
         return ((LanguageItem)value).getProjectTypes() == null || ((LanguageItem)value).getProjectTypes().isEmpty();
      }
      return true;
   }

   public ListDataProvider<Object> getDataProvider()
   {
      return dataProvider;
   }
}
