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
package org.exoplatform.ide.client.project.packaging;

import javax.imageio.ImageReader;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.project.packaging.model.DependencyItem;
import org.exoplatform.ide.client.project.packaging.model.DependencyListItem;
import org.exoplatform.ide.client.project.packaging.model.PackageItem;
import org.exoplatform.ide.client.project.packaging.model.ProjectItem;
import org.exoplatform.ide.client.project.packaging.model.ResourceDirectoryItem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PEItemTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Object>
{

   private String id;

   private String prefixId;

   //   /**
   //    * Get all selected items
   //    * 
   //    * @return List of selected items
   //    */
   //   public List<Object> getSelectedItems()
   //   {
   //      List<Item> items = new ArrayList<Item>();
   //      if (tree.getSelectedItem() != null)
   //      {
   //         items.add((Item)tree.getSelectedItem().getUserObject());
   //      }
   //      return items;
   //   }

   public Object getSelectedObject()
   {
      return tree.getSelectedItem().getUserObject();
   }

   public boolean selectItem(Object item)
   {
      return false;
   }

   @Override
   public void doUpdateValue()
   {
      /*
       * If value == null - clear tree.
       */
      if (value == null)
      {
         if (tree.getItemCount() > 0)
         {
            tree.removeItems();
         }

         return;
      }

      if (value instanceof ProjectItem)
      {
         /*
          * Create root node if tree has not it.
          */
         TreeItem rootTreeItem;
         if (tree.getItemCount() == 0)
         {
            rootTreeItem = createTreeNode(value);
            tree.addItem(rootTreeItem);
         }
         else
         {
            rootTreeItem = tree.getItem(0);
         }

         addProjectItems((ProjectItem)value, rootTreeItem);
      }
      else if (value instanceof ResourceDirectoryItem)
      {
         addResourceDirectoryItems((ResourceDirectoryItem)value);
      }
      else if (value instanceof DependencyListItem)
      {
         addDependencyListItems((DependencyListItem)value);
      }
      else if (value instanceof PackageItem)
      {
         addPackageItems((PackageItem)value);
      }
      else if (value instanceof FolderModel)
      {
         addFolderItems((FolderModel)value);
      }
   }

   private void addProjectItems(ProjectItem projectItem, TreeItem rootTreeItem)
   {
      for (ResourceDirectoryItem resDir : projectItem.getResourceDirectories())
      {
         TreeItem item = createTreeNode(resDir);
         rootTreeItem.addItem(item);
         item.addItem("");
      }

      for (DependencyListItem dependencyListItem : projectItem.getDependencies())
      {
         TreeItem item = createTreeNode(dependencyListItem);
         rootTreeItem.addItem(item);
         item.addItem("");
      }

      for (FolderModel folder : projectItem.getFolders())
      {
         TreeItem item = createTreeNode(folder);
         rootTreeItem.addItem(item);
         item.addItem("");
      }

      for (FileModel file : projectItem.getFiles())
      {
         TreeItem item = createTreeNode(file);
         rootTreeItem.addItem(item);
      }
   }

   private TreeItem getResourceDirectoryTreeItem(String resourceDirectoryName)
   {
      TreeItem rootItem = tree.getItem(0);
      for (int i = 0; i < rootItem.getChildCount(); i++)
      {
         TreeItem treeItem = rootItem.getChild(i);
         if (treeItem.getUserObject() instanceof ResourceDirectoryItem)
         {
            ResourceDirectoryItem resourceDirectoryItem = (ResourceDirectoryItem)treeItem.getUserObject();
            if (resourceDirectoryItem.getName().equals(resourceDirectoryName))
            {
               return treeItem;
            }
         }
      }

      return null;
   }

   private TreeItem getPackageTreeItem(String resourceDirectoryName, String packageName)
   {
      TreeItem resourceDirectoryTreeItem = getResourceDirectoryTreeItem(resourceDirectoryName);
      if (resourceDirectoryTreeItem != null)
      {
         // search package tree item here
         for (int i = 0; i < resourceDirectoryTreeItem.getChildCount(); i++)
         {
            TreeItem treeItem = resourceDirectoryTreeItem.getChild(i);
            if (treeItem.getUserObject() instanceof PackageItem)
            {
               PackageItem packageItem = (PackageItem)treeItem.getUserObject();
               if (packageItem.getPackageName().equals(packageName))
               {
                  return treeItem;
               }
            }
         }

      }

      return null;
   }

   private void addResourceDirectoryItems(ResourceDirectoryItem resourceDirectoryItem)
   {
      TreeItem resourceDirectoryTreeItem = getResourceDirectoryTreeItem(resourceDirectoryItem.getName());
      if (resourceDirectoryTreeItem != null)
      {
         // clear all children
         resourceDirectoryTreeItem.removeItems();

         // add resource directory items here
         for (PackageItem pkg : resourceDirectoryItem.getPackages())
         {
            TreeItem newItem = createTreeNode(pkg);
            resourceDirectoryTreeItem.addItem(newItem);
            if (pkg.getFiles().size() > 0)
            {
               newItem.addItem("");
            }
         }

         for (FileModel file : resourceDirectoryItem.getFiles())
         {
            TreeItem newItem = createTreeNode(file);
            resourceDirectoryTreeItem.addItem(newItem);
         }
      }
   }

   private void addPackageItems(PackageItem packageItem)
   {
      TreeItem packageTreeItem = getPackageTreeItem(packageItem.getResourceDirectory(), packageItem.getPackageName());
      if (packageTreeItem != null)
      {
         // clear children
         packageTreeItem.removeItems();

         // add files
         for (FileModel file : packageItem.getFiles())
         {
            TreeItem fileItem = createTreeNode(file);
            packageTreeItem.addItem(fileItem);
         }
      }
   }

   private void addDependencyListItems(DependencyListItem dependencyListItem)
   {
      TreeItem rootItem = tree.getItem(0);
      for (int i = 0; i < rootItem.getChildCount(); i++)
      {
         TreeItem treeItem = rootItem.getChild(i);
         if (treeItem.getUserObject() instanceof DependencyListItem)
         {
            treeItem.removeItems();

            for (String dependency : dependencyListItem.getDependencies())
            {
               TreeIcon icon = new TreeIcon(IDEImageBundle.INSTANCE.jarReference());
               Widget itemWidget = createItemWidget(icon, dependency);
               TreeItem node = new TreeItem(itemWidget);
               node.setUserObject(dependency);
               treeItem.addItem(node);
            }
         }
      }
   }

   private TreeItem getFolderTreeItem(String path)
   {
      String[] parts = path.split("/");
      TreeItem treeItem = null;

      for (int index = 0; index < parts.length; index++)
      {
         if (index == 0)
         {
            TreeItem item = tree.getItem(0);
            if (item.getUserObject() instanceof ProjectItem && ((ProjectItem)item.getUserObject())
               .getProject().getName().equals(parts[index]))
            {
               treeItem = item;
               continue;
            }

            return null;
         }
         
         boolean founded = false;
         for (int i = 0; i < treeItem.getChildCount(); i++)
         {
            TreeItem item = treeItem.getChild(i);
            if (item.getUserObject() instanceof FolderModel &&
                     ((FolderModel)item.getUserObject()).getName().equals(parts[index]))
            {
               treeItem = item;
               founded = true;
               break;
            }
         }
         
         if (!founded)
         {
            return null;            
         }
      }
      
      return treeItem;
   }

   private void addFolderItems(FolderModel folder)
   {
      String folderPath = folder.getPath();
      if (folderPath.startsWith("/"))
      {
         folderPath = folderPath.substring(1);
      }

      TreeItem folderTreeItem = getFolderTreeItem(folderPath);
      if (folderTreeItem == null)
      {
         return;
      }

      folderTreeItem.removeItems();
      for (Item item : folder.getChildren().getItems())
      {
         TreeItem ti = createTreeNode(item);
         folderTreeItem.addItem(ti);
         if (item instanceof FolderModel)
         {
            ti.addItem(new TreeItem(""));
         }
      }
   }

   private TreeItem createTreeNode(Object item)
   {
      TreeIcon nodeIcon;
      String nodeName;

      if (item instanceof ProjectItem)
      {
         ProjectItem projectItem = (ProjectItem)item;
         //nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.projectOpened());
         ImageResource imageResource = ProjectResolver.getImageForProject(projectItem.getProject().getProjectType());
         nodeIcon = new TreeIcon(imageResource);
         //nodeIcon = ProjectResolver.getImageForProject(((ProjectModel)item).getProjectType());
         nodeName = projectItem.getProject().getName();
      }
      else if (item instanceof ResourceDirectoryItem)
      {
         ResourceDirectoryItem resourceDirectoryItem = (ResourceDirectoryItem)item;
         nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.resourceDirectory());
         nodeName = resourceDirectoryItem.getName();
      }
      else if (item instanceof PackageItem)
      {
         PackageItem packageItem = (PackageItem)item;
         if (packageItem.getFiles().size() == 0)
         {
            nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.packageEmptyFolder());
         }
         else
         {
            nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.packageFolder());
         }
         nodeName = packageItem.getPackageName();
      }
      else if (item instanceof DependencyListItem)
      {
         DependencyListItem dependencyListItem = (DependencyListItem)item;
         nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.jarReferences());
         nodeName = dependencyListItem.getName();
      }
      else if (item instanceof DependencyItem)
      {
         DependencyItem dependencyItem = (DependencyItem)item;
         nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.jarReference());
         nodeName = dependencyItem.getName();
      }
      else if (item instanceof FolderModel)
      {
         FolderModel folder = (FolderModel)item;
         nodeIcon = new TreeIcon(ImageUtil.getIcon(folder.getMimeType()));
         nodeName = folder.getName();
      }
      else if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         nodeIcon = new TreeIcon(ImageUtil.getIcon(file.getMimeType()));
         nodeName = file.getName();
      }
      else
      {
         nodeIcon = new TreeIcon(IDEImageBundle.INSTANCE.defaultFile());
         nodeName = "item type not defined";
      }

      Widget itemWidget = createItemWidget(nodeIcon, nodeName);
      TreeItem node = new TreeItem(itemWidget);
      node.setUserObject(item);

      //      node.getElement().setId(prefixId + Utils.md5(item.getPath()));
      return node;
   }

   protected Widget createItemWidget(TreeIcon treeNodeIcon, String text)
   {
      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      // Image i = new Image(icon);
      //TreeIcon i = new TreeIcon(icon);
      treeNodeIcon.setWidth("16px");
      treeNodeIcon.setHeight("16px");
      grid.setWidget(0, 0, treeNodeIcon);
      // Label l = new Label(text, false);
      HTMLPanel l = new HTMLPanel("div", text);
      l.setStyleName("ide-Tree-label");
      grid.setWidget(0, 1, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");
      // grid.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
      DOM.setStyleAttribute(grid.getElement(), "display", "block");
      return grid;
   }

   public void setTreeGridId(String id)
   {
      this.id = id;
      getElement().setId(id);
   }

   public String getId()
   {
      return id;
   }

   public String getPrefixId()
   {
      return prefixId;
   }

   public void setPrefixId(String prefixId)
   {
      this.prefixId = prefixId;
   }

}
