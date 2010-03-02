/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.wadl.component;

import java.util.ArrayList;

import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.ui.smartgwt.component.TreeGrid;
import org.exoplatform.ideall.client.Images;

import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlResourcesTreeGrid extends TreeGrid<Resource>
{
   private Tree tree;

   private TreeNode rootNode;

   public WadlResourcesTreeGrid()
   {
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      setData(tree);
      setCanFocus(false);
      setCanSort(false);
      this.setTreeFieldTitle("Resources");
      rootNode = new TreeNode("root");
      tree.setRoot(rootNode);

      setShowRoot(false);
      setShowConnectors(false);
   }

   @Override
   protected void doUpdateValue()
   {
      ArrayList<Resource> childs = new ArrayList<Resource>();
      childs.add(getValue());
      rootNode.setChildren(new TreeNode[0]);
      fillTreeItems(rootNode, childs, null);
      tree.openAll();
   }

   /**
    * 
    * @param parentNode
    * @param children
    * @param repeatedPath common part of path for tree node and it's children
    */
   private void fillTreeItems(TreeNode parentNode, ArrayList<Resource> children, String repeatedPath)
   {
      String pathBegging = null;
	   for (Resource child : children)
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
            
            String path = child.getPath();
//            something strange
//            if(!path.substring(0, 1).equals("h"))
//            {
//               path = path.substring(1);    
//            }
            //cuts path begging, which coincides with parent's path
            pathBegging = path;
            if (repeatedPath != null && path.startsWith(repeatedPath)) {
            	path = path.substring(repeatedPath.length());
            	pathBegging = repeatedPath + pathBegging;
            }
            if (path.startsWith("/"))
            	path = path.substring(1);
            
            newNode = new TreeNode(path);
            
            newNode.setAttribute("icon", Images.RepositoryService.SERVICE);
            
            newNode.setAttribute(getValuePropertyName(), child);
            tree.add(newNode, parentNode);
         }

         if (child.getMethodOrResource().size() != 0)
         {
            ArrayList<Resource> childResources = new ArrayList<Resource>();
            for (Object r : child.getMethodOrResource())
            {
               if (r instanceof Resource)
               {
                  childResources.add((Resource)r);
               }
            }
            if (childResources.size() != 0)
            {
               fillTreeItems(newNode, childResources, pathBegging);
            }

         }

      }
   }

}
