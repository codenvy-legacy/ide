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
package org.exoplatform.ide.client.template.ui;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.Tree;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.Template;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: TemplateTree Mar 21, 2011 11:16:32 AM evgen $
 *
 */
public class TemplateTree extends Tree<Template>
{

   private TreeItem rootNode;

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      if (getValue() == null)
      {
         if (tree.getItemCount() != 0)
         {
            tree.removeItems();
         }
         return;
      }

      if (tree.getItemCount() == 0)
      {
         String nodeName = getValue().getName();
         ImageResource icon;
         if (getValue().getIcon() != null)
         {
            icon = getValue().getIcon();
            //            rootNode.setAttribute("icon", getValue().getIcon());
         }
         else
            icon = IDEImageBundle.INSTANCE.folder();
         rootNode = new TreeItem(createTreeNodeWidget(new Image(icon), nodeName));
         rootNode.setUserObject(getValue());
         tree.addItem(rootNode);
         tree.setSelectedItem(rootNode);
      }

      if (getValue() instanceof FolderTemplate)
      {
         FolderTemplate folder = (FolderTemplate)getValue();
         if (folder.getChildren() != null)
         {
            setItems(rootNode, folder.getChildren());
         }
      }
      else if (getValue() instanceof FileTemplate)
      {
         return;
      }
   }

   /**
    * Recursively fills tree with templates.
    * 
    * @param parentNode node, which will be filled
    * @param children list of templates
    */
   private void setItems(TreeItem parentNode, List<Template> children)
   {
      parentNode.removeItems();
      for (Template template : children)
      {
         TreeItem newNode = null;

         for (int i = 0; i < parentNode.getChildCount(); i++)
         {
            TreeItem node = parentNode.getChild(i);
            if (node.getUserObject() == template)
            {
               newNode = node;
               break;
            }
         }

         if (newNode == null)
         {
            //            newNode = new TreeNode(template.getName());
            //            newNode.setAttribute(getValuePropertyName(), template);
            //            newNode.setAttribute(ICON, template.getIcon());
            String nodeName = template.getName();
            if (template instanceof FileTemplate)
            {
               nodeName =
                  ((FileTemplate)template).getFileName() + "(" + IDE.TEMPLATE_CONSTANT.from() + " "
                     + template.getName() + ")";
            }
            newNode = new TreeItem(createTreeNodeWidget(new Image(template.getIcon()), nodeName));
            newNode.setUserObject(template);

            parentNode.addItem(newNode);
            parentNode.setState(true, true);
         }

         if (template instanceof FolderTemplate)
         {
            if (((FolderTemplate)template).getChildren() != null)
            {
               setItems(newNode, ((FolderTemplate)template).getChildren());
            }
         }
      }
   }

   /**
    * Get selected templates in tree grid.
    * 
    * @return List of Templates
    */
   public List<Template> getSelectedItems()
   {
      List<Template> selectedItems = new ArrayList<Template>();

      //      for (ListGridRecord record : getSelection())
      //      {
      //         selectedItems.add((Template)record.getAttributeAsObject(getValuePropertyName()));
      //      }
      if (tree.getSelectedItem() != null)
      {
         selectedItems.add((Template)tree.getSelectedItem().getUserObject());
      }

      return selectedItems;
   }

   private TreeItem getTreeItem(TreeItem parent, Template template)
   {
      if (parent.getUserObject() == template)
         return parent;

      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem node = parent.getChild(i);
         if (node.getUserObject() == template)
         {
            return node;
         }
         TreeItem child = getTreeItem(node, template);
         if (child != null)
            return child;
      }

      return null;
   }

   public void selectTemplate(Template template)
   {
      TreeItem item = getTreeItem(rootNode, template);
      if (item != null)
         tree.setSelectedItem(item, true);
   }

   public void setRootNodeName(String name)
   {
      //      rootNode.setAttribute(NAME, name);
      //      redraw();
   }

   public void updateTree()
   {
      doUpdateValue();
   }

   public String getTemplateLocation(Template template)
   {
      TreeItem item = getTreeItem(rootNode, template);
      if (item != null)
      {
         //         item.getP
      }
      return null;
   }

}
