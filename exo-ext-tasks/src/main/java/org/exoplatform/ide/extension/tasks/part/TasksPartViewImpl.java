/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.extension.tasks.part;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.ui.list.SimpleList;
import org.exoplatform.ide.ui.list.SimpleList.View;
import org.exoplatform.ide.util.dom.Elements;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class TasksPartViewImpl extends Composite implements TasksPartView
{

   private static TasksPartViewImplUiBinder uiBinder = GWT.create(TasksPartViewImplUiBinder.class);

   interface TasksPartViewImplUiBinder extends UiBinder<Widget, TasksPartViewImpl>
   {
   }

   private SimpleList.ListItemRenderer<String> listItemRenderer = new SimpleList.ListItemRenderer<String>()
   {
      @Override
      public void render(Element itemElement, String itemData)
      {
         TableCellElement label = Elements.createTDElement();
         label.setInnerHTML(itemData);
         itemElement.appendChild(label);
      }

      @Override
      public Element createElement()
      {
         return Elements.createTRElement();
      }
   };

   private SimpleList.ListEventDelegate<String> listDelegate = new SimpleList.ListEventDelegate<String>()
   {
      @Override
      public void onListItemClicked(Element itemElement, String itemData)
      {
         list.getSelectionModel().setSelectedItem(itemData);
      }

      @Override
      public void onListItemDoubleClicked(Element listItemBase, String itemData)
      {
         //                     Assert.isNotNull(delegate);
         //                     delegate.onSelect(itemData);
      }
   };

   @UiField
   ScrollPanel mainPanel;

   @UiField
   Button addBtn;

   @UiField
   Button removeBtn;

   private SimpleList<String> list;

   private ActionDelegate delegate;

   /**
    * Because this class has a default constructor, it can
    * be used as a binder template. In other words, it can be used in other
    * *.ui.xml files as follows:
    * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
     *   xmlns:g="urn:import:**user's package**">
    *  <g:**UserClassName**>Hello!</g:**UserClassName>
    * </ui:UiBinder>
    * Note that depending on the widget that is used, it may be necessary to
    * implement HasHTML instead of HasText.
    */
   @Inject
   public TasksPartViewImpl(Resources resources)
   {
      initWidget(uiBinder.createAndBindUi(this));

      TableElement tableElement = Elements.createTableElement();
      tableElement.setAttribute("style", "width: 100%");
      list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

      this.mainPanel.setStyleName(resources.coreCss().simpleListContainer());
      this.mainPanel.add(list);

   }

   @UiHandler(value = {"addBtn"})
   protected void addPressed(ClickEvent clickEvent)
   {
      if (delegate != null)
      {
         delegate.onAddEvent();
      }
   }

   @UiHandler(value = {"removeBtn"})
   protected void removePressed(ClickEvent clickEvent)
   {
      if (delegate != null)
      {
         if (list.getSelectionModel().getSelectedItem() != null)
         {
            delegate.onRemoveEvent(list.getSelectionModel().getSelectedItem());
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void displayTasks(JsonArray<String> tasks)
   {
      list.render(tasks);
   }

}
