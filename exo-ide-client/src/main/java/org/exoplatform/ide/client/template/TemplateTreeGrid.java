/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.template;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.Template;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree grid for displaying project template structure.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class TemplateTreeGrid<T extends Template> extends TreeGrid<T>
{

   private static final String ID = "ideProjectTemplateTreeGrid";
   
   private static final String ICON = "icon";
   
   private static final String NAME = "name";

   private static final String ROOT = "root";
   
   private Tree tree;
   
   private TreeNode rootNode;

   public TemplateTreeGrid()
   {
      setID(ID);

      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      tree.setRoot(new TreeNode(ROOT));
      setData(tree);

      setSelectionType(SelectionStyle.SINGLE);

      setSeparateFolders(true);

      setShowConnectors(false);
      setCanSort(false);

      TreeGridField nameField = new TreeGridField(NAME);

      //TODO
      //This field need for selenium.
      //We can't select tree node, if click on first column.
      //If you click on second column - tree item is selected.
      TreeGridField mockField = new TreeGridField("mock");
      mockField.setWidth(3);
      setFields(nameField, mockField);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid#doUpdateValue()
    */
   @Override
   protected void doUpdateValue()
   {
      if (getValue() == null)
      {
         if (rootNode != null)
         {
            tree.remove(rootNode);
         }
         rootNode = null;
         return;
      }

      if (rootNode == null)
      {
         String nodeName = getValue().getName();
         rootNode = new TreeNode(nodeName);
         rootNode.setAttribute(NAME, getValue().getName());
         rootNode.setAttribute(getValuePropertyName(), getValue());

         if (getValue().getIcon() != null)
         {
            rootNode.setAttribute("icon", getValue().getIcon());
         }

         rootNode.setIsFolder(true);
         tree.add(rootNode, tree.getRoot());
         selectRecord(rootNode);
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
   private void setItems(TreeNode parentNode, List<Template>children)
   {
      TreeNode[] oldNodes = tree.getChildren(parentNode);
      tree.removeList(oldNodes);
      for (Template template : children)
      {
         TreeNode newNode = null;
         TreeNode[] nodes = tree.getChildren(parentNode);
         for (TreeNode node : nodes)
         {
            if (node.getAttributeAsObject(getValuePropertyName()) == template)
            {
               newNode = node;
               break;
            }
         }

         if (newNode == null)
         {
            newNode = new TreeNode(template.getName());
            newNode.setAttribute(getValuePropertyName(), template);
            newNode.setAttribute(ICON, template.getIcon());
            String nodeName = template.getName();
            if (template instanceof FileTemplate)
            {
               nodeName = ((FileTemplate)template).getFileName() + "(from " + template.getName() + ")";
            }
            newNode.setAttribute(NAME, nodeName);
            tree.add(newNode, parentNode);
            tree.openFolder(parentNode);
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

      for (ListGridRecord record : getSelection())
      {
         selectedItems.add((Template)record.getAttributeAsObject(getValuePropertyName()));
      }

      return selectedItems;
   }
   
   public void selectTemplate(Template template)
   {
      for (ListGridRecord record : getRecords())
      {
         if (template == (Template)record.getAttributeAsObject(getValuePropertyName()))
         {
            selectSingleRecord(record);
         }
      }

   }
   
   public void setRootNodeName(String name)
   {
      rootNode.setAttribute(NAME, name);
      redraw();
   }
   
   public void updateTree()
   {
      doUpdateValue();
   }
   
   public String getTemplateLocation(Template template)
   {
      for (TreeNode node : tree.getAllNodes())
      {
         if (((Template)node.getAttributeAsObject(getValuePropertyName())).equals(template))
         {
            return tree.getPath(node);
         }
      }
      return null;
   }
   
}
