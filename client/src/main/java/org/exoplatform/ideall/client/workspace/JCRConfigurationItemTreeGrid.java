/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ideall.client.workspace;

import java.util.ArrayList;

import org.exoplatform.gwt.commons.smartgwt.component.TreeGrid;

import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class JCRConfigurationItemTreeGrid extends TreeGrid<JCRConfigurationItem>
{

   private Tree tree;

   private TreeNode rootNode;

   public JCRConfigurationItemTreeGrid()
   {
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      setData(tree);
      setCanFocus(false);
      setCanSort(false);

      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);

      setShowRoot(false);
      setShowConnectors(true);
   }

   @Override
   protected void doUpdateValue()
   {
      ArrayList<JCRConfigurationItem> childs = new ArrayList<JCRConfigurationItem>();
      childs.add(getValue());
      rootNode.setChildren(new TreeNode[0]);
      fillTreeItems(rootNode, childs);
      tree.openAll();
   }

   private void fillTreeItems(TreeNode parentNode, ArrayList<JCRConfigurationItem> children)
   {
      for (JCRConfigurationItem child : children)
      {
         TreeNode newNode = null;
         TreeNode[] nodes = tree.getChildren(parentNode);
         for (TreeNode node : nodes)
         {
            if (node.getAttributeAsObject(getValuePropertyName()) == child)
            {
               newNode = node;
               break;
            }
         }

         if (newNode == null)
         {
            newNode = new TreeNode(child.getName());
            if (child.getIcon() != null)
            {
               newNode.setAttribute("icon", child.getIcon());
            }
            newNode.setAttribute(getValuePropertyName(), child);
            tree.add(newNode, parentNode);
         }

         fillTreeItems(newNode, child.getChildren());
      }

   }

}
