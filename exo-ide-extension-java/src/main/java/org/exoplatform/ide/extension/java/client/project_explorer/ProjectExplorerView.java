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

package org.exoplatform.ide.extension.java.client.project_explorer;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.client.CellTreeResource;
import org.exoplatform.ide.extension.java.client.JavaClientBundle;
import org.w3c.dom.events.MouseEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerView extends ViewImpl implements org.exoplatform.ide.extension.java.client.project_explorer.ProjectExplorerPresenter.Display
{

   private static ProjectExplorerViewUiBinder uiBinder = GWT.create(ProjectExplorerViewUiBinder.class);

   interface ProjectExplorerViewUiBinder extends UiBinder<Widget, ProjectExplorerView>
   {
   }
   
   @UiField
   ScrollPanel scrollPanel;
   
   private Tree astTree;

   public ProjectExplorerView()
   {
      super("projectExplorer", ViewType.NAVIGATION, "Project Explorer", new Image(JavaClientBundle.INSTANCE.javaProject()));
      add(uiBinder.createAndBindUi(this));
   }
   
   private class Tree extends CellTree implements HasDoubleClickHandlers
   {
      
      public <T>Tree(TreeViewModel viewModel, T rootValue, Resources resources) {
         super(viewModel, rootValue, resources);
         
         sinkEvents(Event.ONDBLCLICK);
      }
      
      @Override
      public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
      {
         return astTree.addHandler(handler, DoubleClickEvent.getType());
      }
      
   };

   @Override
   public void initializeASTTree(ASTTreeViewModel viewModel, SelectionModel<?> selectionModel)
   {
      CellTreeResource resources = GWT.create(CellTreeResource.class);
      
      astTree = new Tree(viewModel, null, resources);
      astTree.getElement().setId("projectExplorerTree");
      astTree.setWidth("100%");
      astTree.setHeight("100%");
      astTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
      astTree.setAnimationEnabled(true);
      scrollPanel.add(astTree);
   }

   @Override
   public HasDoubleClickHandlers getProjectCellTree()
   {
      return astTree;
   }

}
