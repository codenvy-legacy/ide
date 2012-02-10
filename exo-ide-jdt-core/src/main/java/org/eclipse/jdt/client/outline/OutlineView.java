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
package org.eclipse.jdt.client.outline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.List;

/**
 * View for Java Outline tree.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:29:29 PM anya $
 * 
 */
public class OutlineView extends ViewImpl implements OutlinePresenter.Display
{
   /**
    * Scroll panel, which contains tree.
    */
   private ScrollPanel scrollPanel;

   private CellTree.Resources res = GWT.create(CellTreeResource.class);

   private CellTree cellTree;

   private OutlineTreeViewModel outlineTreeViewModel;

   private SingleSelectionModel<Object> selectionModel;

   public OutlineView()
   {
      // TODO Fix view properties
      super("OutlineViewId", ViewType.INFORMATION, "Java Outline");
      selectionModel = new SingleSelectionModel<Object>();
      scrollPanel = new ScrollPanel();

      outlineTreeViewModel = new OutlineTreeViewModel(selectionModel);
      cellTree = new CellTree(outlineTreeViewModel, null, res);
      cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

      scrollPanel.add(cellTree);
      add(scrollPanel);
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#updateOutline(org.eclipse.jdt.client.core.dom.CompilationUnit)
    */
   @Override
   public void updateOutline(CompilationUnit cUnit)
   {
      outlineTreeViewModel.getDataProvider().getList().clear();
      GetChildrenVisitor visitor = new GetChildrenVisitor();
      visitor.visit(cUnit);
      outlineTreeViewModel.getDataProvider().getList().addAll(visitor.getNodes());
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#getSingleSelectionModel()
    */
   @Override
   public SingleSelectionModel<Object> getSingleSelectionModel()
   {
      return selectionModel;
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#selectNode(org.eclipse.jdt.client.core.dom.ASTNode)
    */
   @Override
   public void selectNode(ASTNode node)
   {
      selectionModel.setSelected(node, true);
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#focusInTree()
    */
   @Override
   public void focusInTree()
   {
      cellTree.setFocus(true);
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#getNodes()
    */
   @Override
   public List<Object> getNodes()
   {
      return outlineTreeViewModel.getDataProvider().getList();
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#openNode(java.lang.Object)
    */
   @Override
   public void openNode(Object object)
   {
      //TODO
   }
}
