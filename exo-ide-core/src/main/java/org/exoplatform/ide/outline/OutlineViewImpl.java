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
package org.exoplatform.ide.outline;

import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.outline.OutlineImpl.OutlineView;
import org.exoplatform.ide.tree.NodeRenderer;
import org.exoplatform.ide.tree.Tree;
import org.exoplatform.ide.tree.Tree.Listener;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OutlineViewImpl implements OutlineView
{

   private Tree<CodeBlock> tree;

   /**
    * @param codeBlockDataAdapter 
    * @param resources 
    * @param renderer
    */
   public OutlineViewImpl(Resources resources, CodeBlockDataAdapter codeBlockDataAdapter, NodeRenderer<CodeBlock> renderer)
   {
      tree = Tree.create(resources, codeBlockDataAdapter, renderer);
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
   public void renderTree()
   {
      tree.renderTree();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void rootChanged(CodeBlock newRoot)
   {
      tree.replaceSubtree(tree.getModel().getRoot(), newRoot, false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setTreeEventHandler(Listener<CodeBlock> listener)
   {
      tree.setTreeEventHandler(listener);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void selectAndExpand(CodeBlock block)
   {
      tree.autoExpandAndSelectNode(block, false);
   }

}
