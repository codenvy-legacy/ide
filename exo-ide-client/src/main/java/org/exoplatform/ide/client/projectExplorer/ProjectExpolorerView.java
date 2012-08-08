/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.projectExplorer;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.google.inject.Inject;

import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter.Display;

import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 26, 2012  
 */
public class ProjectExpolorerView extends Composite implements Display
{

   private class DoubleClickableCellTree extends CellTree implements HasDoubleClickHandlers
   {
      public <T> DoubleClickableCellTree(TreeViewModel viewModel, T rootValue)
      {
         super(viewModel, rootValue);
      }

      public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
      {
         return this.addDomHandler(handler, DoubleClickEvent.getType());
      }

   }

   private class ProjectExplorerTreeModel implements TreeViewModel
   {

      final AsyncDataProvider<String> asyncDataProvider = new AsyncDataProvider<String>()
      {
         @Override
         protected void onRangeChanged(HasData<String> display)
         {
         }
      };

      final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();

      public <T> NodeInfo<?> getNodeInfo(T value)
      {
         // Return a node info that pairs the data with a cell.
         return new DefaultNodeInfo<String>(asyncDataProvider, new TextCell(), selectionModel, null);
      }

      public boolean isLeaf(Object value)
      {
         // The maximum length of a value is ten characters.
         return value != null;
      }

   }

   interface ProjectExpolorerViewUiBinder extends UiBinder<Widget, ProjectExpolorerView>
   {
   }

   private static ProjectExpolorerViewUiBinder uiBinder = GWT.create(ProjectExpolorerViewUiBinder.class);

   private ProjectExplorerTreeModel treeModel;

   @UiField(provided = true)
   CellTree cellTree;

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
   public ProjectExpolorerView()
   {
      treeModel = new ProjectExplorerTreeModel();
      cellTree = new DoubleClickableCellTree(treeModel, null);

      // hack: disable text selection on doubleClick
      cellTree.addDomHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.preventDefault();
         }
      }, MouseDownEvent.getType());

      initWidget(uiBinder.createAndBindUi(this));
   }

   /**
    * {@inheritDoc}
    */
   public String getSelectedFileName()
   {
      return treeModel.selectionModel.getSelectedObject();
   }

   /**
   * {@inheritDoc}
   */
   public HasDoubleClickHandlers getTree()
   {
      return (DoubleClickableCellTree)cellTree;
   }

   /**
    * {@inheritDoc}
    */
   public void setItems(List<String> fileNames)
   {
      //treeModel.asyncDataProvider.updateRowCount(fileNames.size(), true);
      for (HasData<String> display : treeModel.asyncDataProvider.getDataDisplays())
      {
         display.setVisibleRange(0, fileNames.size());
         display.setRowData(0, fileNames);
      }
      //treeModel.asyncDataProvider.updateRowData(0, fileNames);
   }
}
