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

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import elemental.html.DragEvent;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.tree.Tree;
import org.exoplatform.ide.tree.TreeNodeElement;

/**
 * TODO: Use UIBinder, when GWT Widget wrappers are introduced for Collide UI elements (Tree)  
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ProjectExplorerViewImpl implements ProjectExplorerView
{
   protected Tree<Resource> tree;

   protected ActionDelegate delegate;

   /**
    * Create view.
    * 
    * @param resources
    */
   @Inject
   public ProjectExplorerViewImpl(Resources resources)
   {
      tree = Tree.create(resources);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Widget asWidget()
   {
      return tree.asWidget();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setItems(Resource resource)
   {
      tree.getModel().setRoot(resource);
      tree.renderTree();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(final ActionDelegate delegate)
   {
      this.delegate = delegate;
      tree.setTreeEventHandler(new org.exoplatform.ide.tree.Tree.Listener<Resource>()
      {

         @Override
         public void onNodeAction(TreeNodeElement<Resource> node)
         {
            delegate.onNodeAction(node.getData());
         }

         @Override
         public void onNodeClosed(TreeNodeElement<Resource> node)
         {
         }

         @Override
         public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Resource> node)
         {
         }

         @Override
         public void onNodeDragStart(TreeNodeElement<Resource> node, DragEvent event)
         {
         }

         @Override
         public void onNodeDragDrop(TreeNodeElement<Resource> node, DragEvent event)
         {
         }

         @Override
         public void onNodeExpanded(TreeNodeElement<Resource> node)
         {
         }

         @Override
         public void onRootContextMenu(int mouseX, int mouseY)
         {
         }

         @Override
         public void onRootDragDrop(DragEvent event)
         {
         }
      });
   }
}
