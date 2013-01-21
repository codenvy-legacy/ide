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
package org.exoplatform.ide.command;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.ui.list.SimpleList;
import org.exoplatform.ide.ui.list.SimpleList.View;
import org.exoplatform.ide.util.dom.Elements;
import org.exoplatform.ide.util.loging.Log;

/**
 * Command that handles the process of project opening. It shows dilog with all the project available and 
 * allows user to open one. 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class OpenProjectCommand implements ExtendedCommand
{
   private final ResourceProvider resourceProvider;

   private SimpleList<String> list;

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
         Log.info(this.getClass(), "onListItemClicked ", itemElement);
         list.getSelectionModel().setSelectedItem(itemData);
      }

      @Override
      public void onListItemDoubleClicked(Element listItemBase, String itemData)
      {
         Log.info(this.getClass(), "onListItemDoubleClicked ", itemData);
         //                     Assert.isNotNull(delegate);
         //                     delegate.onSelect(itemData);
      }
   };

   private final Resources resources;

   /**
    * Instantiates command
    */
   @Inject
   public OpenProjectCommand(ResourceProvider resourceProvider, Resources resources)
   {
      this.resourceProvider = resourceProvider;
      this.resources = resources;

      TableElement tableElement = Elements.createTableElement();
      tableElement.setAttribute("style", "width: 100%");
      list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {

      resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>()
      {
         @Override
         public void onSuccess(JsonArray<String> result)
         {
            final PopupPanel dialogBox = createDialog(result);
            dialogBox.center();
            dialogBox.show();
         }

         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(OpenProjectCommand.class, "can't list projects", caught);
         }
      });

   }

   /**
    * TODO remove dialog from here
    * 
    * @return Dialog widget
    */
   public PopupPanel createDialog(JsonArray<String> projects)
   {

      final DialogBox dialogBox = new DialogBox();
      dialogBox.setText("Open the project");

      ScrollPanel listPanel = new ScrollPanel();
      listPanel.setStyleName(resources.coreCss().simpleListContainer());
      listPanel.add(list);
      dialogBox.setTitle("Select a project");
      dialogBox.setText("Select a project, please");

      DockLayoutPanel content = new DockLayoutPanel(Unit.PX);
      content.setSize("300px", "300px");
      FlowPanel bottomPanel = new FlowPanel();
      content.addSouth(bottomPanel, 24);
      content.add(listPanel);

      dialogBox.setWidget(content);

      Button closeButton = new Button("cancel", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            dialogBox.hide();
         }
      });
      Button okButton = new Button("ok", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            Log.info(this.getClass(), "onClick = ", list.getSelectionModel().getSelectedItem());
            if (list.getSelectionModel().getSelectedItem() != null)
            {
               String selectedItem = list.getSelectionModel().getSelectedItem();
               resourceProvider.getProject(selectedItem, new AsyncCallback<Project>()
               {
                  @Override
                  public void onSuccess(Project result)
                  {
                     dialogBox.hide();
                  }

                  @Override
                  public void onFailure(Throwable caught)
                  {
                     Log.error(OpenProjectCommand.class, "can't open projects", caught);
                  }
               });
            }
         }
      });
      bottomPanel.add(closeButton);
      bottomPanel.add(okButton);
      list.render(projects);
      return dialogBox;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Image getIcon()
   {
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Expression inContext()
   {
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public Expression canExecute()
   {
      return null;
   }
}
