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
package org.eclipse.jdt.client.packaging;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;

import org.eclipse.jdt.client.packaging.model.Dependencies;
import org.eclipse.jdt.client.packaging.model.Project;
import org.eclipse.jdt.client.packaging.model.ResourceDirectory;
import org.eclipse.jdt.client.packaging.model.next.ClasspathFolder;
import org.eclipse.jdt.client.packaging.model.next.JavaProject;
import org.eclipse.jdt.client.packaging.model.next.Package;
import org.eclipse.jdt.client.packaging.model.next.SourceDirectory;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PEItemTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item> implements
   OpenHandler<Item>
{

   private String id;

   private String prefixId;

   public PEItemTree()
   {
      sinkEvents(Event.ONCONTEXTMENU);
      addOpenHandler(this);
   }

   /**
    * @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event)
    */
   @Override
   public void onBrowserEvent(Event event)
   {
      if (Event.ONCONTEXTMENU == DOM.eventGetType(event))
      {
         NativeEvent nativeEvent =
            Document.get().createMouseDownEvent(-1, event.getScreenX(), event.getScreenY(), event.getClientX(),
               event.getClientY(), event.getCtrlKey(), event.getAltKey(), event.getShiftKey(), event.getMetaKey(),
               NativeEvent.BUTTON_LEFT);
         DOM.eventGetTarget(event).dispatchEvent(nativeEvent);
      }
      super.onBrowserEvent(event);
   }

   public Object getSelectedObject()
   {
      return tree.getSelectedItem().getUserObject();
   }

   public boolean selectItem(Item item)
   {
      refreshProject(project, item);

      PackageExplorerTreeItem treeItem = treeItems.get(item.getId());
      if (treeItem == null)
      {
         return false;
      }

      if (treeItem.getParentItem() == null)
      {
         return false;
      }

      try
      {
         tree.setSelectedItem(treeItem);
         tree.ensureSelectedItemVisible();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      treeItem = treeItems.get(item.getId());
      tree.setSelectedItem(treeItem);
      return true;
   }
   
   private void refreshProject(JavaProject project, Item item)
   {
      //System.out.println("refresh JavaProject " + project.getPath());
      
      /*
       * Refresh java project tree node
       */
      PackageExplorerTreeItem treeItem = treeItems.get(project.getId());
      treeItem.setUserObject(project);
      treeItem.render();

      /*
       * Remove nonexistent tree items.
       */
      /*
      ArrayList<String> projectItems = new ArrayList<String>();
      for (ClasspathFolder classpathFolder : project.getClasspathFolders())
      {
         projectItems.add(classpathFolder.getId());
      }
      for (project.getChildren())
      {
      }
      
      for (Package _package : sourceDirectory.getPackages())
      {
         sourceDirectoryItems.add(_package.getId());
      }
      for (FileModel file : sourceDirectory.getDefaultPackage().getFiles())
      {
         sourceDirectoryItems.add(file.getId());
      }

      ArrayList<TreeItem> removeItems = new ArrayList<TreeItem>();
      for (int i = 0; i < treeItem.getChildCount(); i++)
      {
         TreeItem ti = treeItem.getChild(i);
         if (!(ti instanceof PackageExplorerTreeItem))
         {
            removeItems.add(ti);
            continue;
         }
         
         PackageExplorerTreeItem child = (PackageExplorerTreeItem)ti;
         if (!sourceDirectoryItems.contains(((Item)child.getUserObject()).getId()))
         {
            removeItems.add(child);
         }
         else if (child.getUserObject() instanceof Package && !isNeedShowPackage((Package)child.getUserObject()))
         {
            removeItems.add(child);
         }
      }

      for (TreeItem child : removeItems)
      {
         treeItem.removeItem(child);
      }
      */
      
      for (ProjectModel module : project.getModules())
      {
         if (item.getPath().startsWith(module.getPath()))
         {
            if (module instanceof JavaProject)
            {
               refreshProject((JavaProject)module, item);
               return;
            }
            else
            {
               new Exception("Project " + module.getPath() + " is not Java project").printStackTrace();
               return;
            }
         }
      }

      for (SourceDirectory sourceDirectory : project.getSourceDirectories())
      {
         if (item.getPath().startsWith(sourceDirectory.getPath()))
         {
            refreshSourceDirectory(sourceDirectory, item);
            return;
         }
      }

      for (Item child : project.getChildren().getItems())
      {
         if (child instanceof FolderModel)
         {
            refreshFolder((FolderModel)child, item);
            return;
         }
      }
   }

   private void refreshSourceDirectory(SourceDirectory sourceDirectory, Item item)
   {
      //System.out.println("refresh SourceDirectory " + sourceDirectory.getPath());
      
      /*
       * Refresh source directory tree node
       */
      PackageExplorerTreeItem treeItem = treeItems.get(sourceDirectory.getId());
      treeItem.setUserObject(sourceDirectory);
      treeItem.setState(true);
      treeItem.render();
      
      /*
       * Remove nonexistent tree items.
       */
      ArrayList<String> sourceDirectoryItems = new ArrayList<String>();
      for (Package _package : sourceDirectory.getPackages())
      {
         sourceDirectoryItems.add(_package.getId());
      }
      for (FileModel file : sourceDirectory.getDefaultPackage().getFiles())
      {
         sourceDirectoryItems.add(file.getId());
      }

      ArrayList<TreeItem> removeItems = new ArrayList<TreeItem>();
      for (int i = 0; i < treeItem.getChildCount(); i++)
      {
         TreeItem ti = treeItem.getChild(i);
         if (!(ti instanceof PackageExplorerTreeItem))
         {
            removeItems.add(ti);
            continue;
         }
         
         PackageExplorerTreeItem child = (PackageExplorerTreeItem)ti;
         if (!sourceDirectoryItems.contains(((Item)child.getUserObject()).getId()))
         {
            removeItems.add(child);
         }
         else if (child.getUserObject() instanceof Package && !isNeedShowPackage((Package)child.getUserObject()))
         {
            removeItems.add(child);
         }
      }

      for (TreeItem child : removeItems)
      {
         treeItem.removeItem(child);
      }

      /*
       * Add missing tree items.
       */
      int index = 0;
      org.eclipse.jdt.client.packaging.model.next.Package defaultPackage = null;
      for (org.eclipse.jdt.client.packaging.model.next.Package _package : sourceDirectory.getPackages())
      {
         if (_package.getPackageName().isEmpty())
         {
            defaultPackage = _package;
            continue;
         }

         PackageExplorerTreeItem child = treeItem.getChildByItemId(_package.getId());
         if (child == null)
         {
            if (isNeedShowPackage(_package))
            {
               child = new PackageExplorerTreeItem(_package);
               treeItem.insertItem(index, child);
               treeItems.put(_package.getId(), child);
               index++;
            }
         }
         else
         {
            child.setUserObject(_package);
            child.render();
            index++;
         }
      }
      
      for (FileModel file : defaultPackage.getFiles())
      {
         PackageExplorerTreeItem child = treeItem.getChildByItemId(file.getId());
         if (child == null)
         {
            child = new PackageExplorerTreeItem(file);
            treeItem.insertItem(index, child);
            treeItems.put(file.getId(), child);
         }
         else
         {
            child.setUserObject(file);
            child.render();
         }
         index++;
      }
      
      /*
       * Refresh packages
       */
      for (org.eclipse.jdt.client.packaging.model.next.Package _package : sourceDirectory.getPackages())
      {
         if (_package.getPackageName().isEmpty())
         {
            continue;
         }

         if (item instanceof FileModel)
         {
            if (_package.getPath().equals(((FileModel)item).getParent().getPath()))
            {
               refreshPackage(_package, item);
               return;
            }
         }
         else if (item instanceof FolderModel)
         {
         }
      }
   }
   
   private void refreshPackage(org.eclipse.jdt.client.packaging.model.next.Package _package, Item item)
   {
      //System.out.println("refresh Package " + _package.getPath());
      
      PackageExplorerTreeItem treeItem = treeItems.get(_package.getId());
      treeItem.setUserObject(_package);
      treeItem.setState(true);
      treeItem.render();

      int index = 0;
      for (FileModel file :_package.getFiles())
      {
         PackageExplorerTreeItem child = treeItem.getChildByItemId(file.getId());
         if (child == null)
         {
            child = new PackageExplorerTreeItem(file);
            treeItem.insertItem(index, child);
            treeItems.put(file.getId(), child);
         }
         else
         {
            child.setUserObject(file);
            child.render();
         }
         index++;
      }

      
   }
   
   private void refreshFolder(FolderModel folder, Item item)
   {
      //System.out.println("refresh FolderModel " + folder.getPath());

   }

   
   
   private JavaProject project;

   private Map<String, PackageExplorerTreeItem> treeItems = new HashMap<String, PackageExplorerTreeItem>();

   @Override
   public void onOpen(OpenEvent<Item> event)
   {
      if (event.getTarget() instanceof FolderModel)
      {
         openFolder((FolderModel)event.getTarget());
      }
   }

   private void updateJavaProject(JavaProject javaProject)
   {
      PackageExplorerTreeItem projectTreeItem = treeItems.get(javaProject.getId());
      if (projectTreeItem == null)
      {
         return;
      }
      projectTreeItem.setUserObject(javaProject);

      for (ProjectModel module : javaProject.getModules())
      {
         if (module instanceof JavaProject)
         {
            updateJavaProject((JavaProject)module);
         }
      }

      for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories())
      {
         updateSourceDirectory(sourceDirectory);
      }

      for (Item item : javaProject.getChildren().getItems())
      {
         updateItem(item);
      }
   }

   private void updateSourceDirectory(SourceDirectory sourceDirectory)
   {
      PackageExplorerTreeItem treeItem = treeItems.get(sourceDirectory.getId());
      if (treeItem == null)
      {
         return;
      }
      treeItem.setUserObject(sourceDirectory);

      for (org.eclipse.jdt.client.packaging.model.next.Package pack : sourceDirectory.getPackages())
      {
         if (pack.getPackageName().isEmpty())
         {
            continue;
         }

         updatePackage(pack);
      }
   }

   private void updatePackage(org.eclipse.jdt.client.packaging.model.next.Package pack)
   {
      PackageExplorerTreeItem treeItem = treeItems.get(pack.getId());
      if (treeItem == null)
      {
         return;
      }

      treeItem.setUserObject(pack);

      for (FileModel file : pack.getFiles())
      {
         updateItem(file);
      }
   }

   private void updateItem(Item item)
   {
      PackageExplorerTreeItem treeItem = treeItems.get(item.getId());
      if (treeItem == null)
      {
         return;
      }

      if (treeItem.getUserObject() instanceof SourceDirectory)
      {
         return;
      }

      if (treeItem.getUserObject() instanceof org.eclipse.jdt.client.packaging.model.next.Package)
      {
         return;
      }

      treeItem.setUserObject(item);

      if (item instanceof FolderModel)
      {
         for (Item child : ((FolderModel)item).getChildren().getItems())
         {
            updateItem(child);
         }
      }
   }

   @Override
   public void doUpdateValue()
   {
      /*
       * Clear tree is value is null
       */
      if (value == null)
      {
         project = null;
         tree.removeItems();
         treeItems.clear();
         return;
      }

      /*
       * 
       */
      if (project == null && !(value instanceof JavaProject))
      {
         return;
      }

      boolean openTreeNode = false;

      if (project == null)
      {
         project = (JavaProject)value;
         PackageExplorerTreeItem rootTreeItem = new PackageExplorerTreeItem(value);
         tree.addItem(rootTreeItem);
         treeItems.put(value.getId(), rootTreeItem);
         openTreeNode = true;
      }

      if (!(value instanceof FolderModel))
      {
         return;
      }

      updateJavaProject(project);
      if (openTreeNode)
      {
         openFolder((FolderModel)value);
      }
      else
      {
         PackageExplorerTreeItem treeItem = treeItems.get(value.getId());
         if (treeItem != null && treeItem.getState())
         {
            openFolder((FolderModel)value);
         }
      }

      if (tree.getSelectedItem() != null)
      {
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               moveHighlight(tree.getSelectedItem());
            }
         });
      }
   }

   private void openFolder(FolderModel folder)
   {
      PackageExplorerTreeItem treeItem = treeItems.get(folder.getId());

      if (treeItem == null)
      {
         return;
      }

      if (treeItem.getUserObject() instanceof JavaProject)
      {
         openJavaProject((JavaProject)treeItem.getUserObject(), treeItem);
      }
      else if (treeItem.getUserObject() instanceof SourceDirectory)
      {
         openSourceDirectory((SourceDirectory)treeItem.getUserObject(), treeItem);
      }
      else if (treeItem.getUserObject() instanceof org.eclipse.jdt.client.packaging.model.next.Package)
      {
         openPackage((org.eclipse.jdt.client.packaging.model.next.Package)treeItem.getUserObject(), treeItem);
      }
      else
      {
         openProjectFolder(folder, treeItem);
      }

      //      if (folder instanceof JavaProject)
      //      {
      //         openJavaProject((JavaProject)folder, treeItem);
      //      }
      //      else if (folder instanceof SourceDirectory)
      //      {
      //         openSourceDirectory((SourceDirectory)folder, treeItem);
      //      }
      //      else if (folder instanceof org.eclipse.jdt.client.packaging.model.next.Package)
      //      {
      //         openPackage((org.eclipse.jdt.client.packaging.model.next.Package)folder, treeItem);
      //      }
      //      else if (folder instanceof FolderModel)
      //      {
      //         if (treeItem.getUserObject() instanceof JavaProject)
      //         {
      //            openJavaProject((JavaProject)treeItem.getUserObject(), treeItem);
      //         }
      //         else if (treeItem.getUserObject() instanceof SourceDirectory)
      //         {
      //            openSourceDirectory((SourceDirectory)treeItem.getUserObject(), treeItem);
      //         }
      //         else if (treeItem.getUserObject() instanceof org.eclipse.jdt.client.packaging.model.next.Package)
      //         {
      //            openPackage((org.eclipse.jdt.client.packaging.model.next.Package)treeItem.getUserObject(), treeItem);
      //         }
      //         else
      //         {            
      //            openProjectFolder(folder, treeItem);
      //         }
      //      }
      //      
      treeItem.setState(true, false);
   }

   private void openJavaProject(JavaProject javaProject, PackageExplorerTreeItem projectItem)
   {
      projectItem.removeItems();

      // modules
      for (ProjectModel module : javaProject.getModules())
      {
         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(module);
         projectItem.addItem(treeItem);
         treeItems.put(module.getId(), treeItem);
         treeItem.addItem("");
      }

      // source directories
      for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories())
      {
         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(sourceDirectory);
         projectItem.addItem(treeItem);
         treeItems.put(sourceDirectory.getId(), treeItem);
         if (sourceDirectory.getPackages().size() > 1 || !sourceDirectory.getPackages().get(0).getFiles().isEmpty())
         {
            treeItem.addItem("");
         }
      }

      // dependencies
//      System.out.println("classpath folders > " + javaProject.getClasspathFolders());
//      for (ClasspathFolder classpathFolder : javaProject.getClasspathFolders())
//      {
//         System.out.println("classpath folder > " + classpathFolder.getName());
//      }

      // other files and folders
      for (Item item : javaProject.getChildren().getItems())
      {
         if (item instanceof JavaProject)
         {
            continue;
         }

         if (isSource(item, javaProject))
         {
            continue;
         }

         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(item);
         projectItem.addItem(treeItem);
         treeItems.put(item.getId(), treeItem);

         if (item instanceof FolderModel && !((FolderModel)item).getChildren().getItems().isEmpty())
         {
            treeItem.addItem("");
         }
      }
   }

   private boolean isSource(Item item, ProjectModel proj)
   {
      if (!(proj instanceof JavaProject))
      {
         return false;
      }

      JavaProject javaProject = (JavaProject)proj;
      for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories())
      {
         if (item.getPath().startsWith(sourceDirectory.getPath()))
         {
            return true;
         }
      }

      return false;
   }

   private void openSourceDirectory(SourceDirectory sourceDirectory, PackageExplorerTreeItem sourceDirectoryTreeItem)
   {
      //    if (projectItem.getChildCount() == 1 && !(projectItem.getChild(0) instanceof PackageExplorerTreeItem))
      //    {
      //       projectItem.removeItems();
      //    }
      sourceDirectoryTreeItem.removeItems();

      org.eclipse.jdt.client.packaging.model.next.Package defaultPackage = null;

      for (org.eclipse.jdt.client.packaging.model.next.Package pack : sourceDirectory.getPackages())
      {
         if (pack.getPackageName().isEmpty())
         {
            defaultPackage = pack;
            continue;
         }

         if (!isNeedShowPackage(pack))
         {
            continue;
         }

         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(pack);
         sourceDirectoryTreeItem.addItem(treeItem);
         treeItems.put(pack.getId(), treeItem);

         if (!pack.getFiles().isEmpty())
         {
            treeItem.addItem("");
         }
      }

      if (defaultPackage != null)
      {
         for (FileModel file : defaultPackage.getFiles())
         {
            PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(file);
            sourceDirectoryTreeItem.addItem(treeItem);
            treeItems.put(file.getId(), treeItem);
         }
      }
   }

   private boolean isNeedShowPackage(org.eclipse.jdt.client.packaging.model.next.Package pack)
   {
      boolean hasFiles = false;
      boolean hasFolders = false;

      for (Item item : pack.getChildren().getItems())
      {
         if (item instanceof FolderModel)
         {
            hasFolders = true;
         }
         else if (item instanceof FileModel)
         {
            hasFiles = true;
         }
      }

      if (hasFolders == true && hasFiles == false)
      {
         return false;
      }

      return true;
   }

   private void openPackage(org.eclipse.jdt.client.packaging.model.next.Package pack,
      PackageExplorerTreeItem packageTreeItem)
   {
      packageTreeItem.removeItems();

      for (FileModel file : pack.getFiles())
      {
         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(file);
         packageTreeItem.addItem(treeItem);
         treeItems.put(file.getId(), treeItem);
      }
   }

   private void openProjectFolder(FolderModel folder, PackageExplorerTreeItem folderTreeItem)
   {
      folderTreeItem.removeItems();

      for (Item item : folder.getChildren().getItems())
      {
         if (isSource(item, folder.getProject()))
         {
            continue;
         }

         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(item);
         folderTreeItem.addItem(treeItem);
         treeItems.put(item.getId(), treeItem);

         if (item instanceof FolderModel)
         {
            if (hasChildren(folder))
            {
               treeItem.addItem("");
            }
         }
      }
   }

   private boolean hasChildren(FolderModel folder)
   {
      for (Item item : folder.getChildren().getItems())
      {
         if (!isSource(item, folder.getProject()))
         {
            return true;
         }
      }

      return false;
   }

   private void closeFolder()
   {
   }

   //   private TreeItem openTreeNode(Object value, boolean open, boolean clearChildren)
   //   {
   //      TreeItem treeItem = null;
   //      if (value instanceof Project)
   //      {
   //         treeItem = addProjectItems((Project)value, clearChildren);
   //      }
   //      else if (value instanceof ResourceDirectory)
   //      {
   //         treeItem = addResourceDirectoryItems((ResourceDirectory)value, clearChildren);
   //      }
   //      else if (value instanceof Dependencies)
   //      {
   //         treeItem = addDependencyListItems((Dependencies)value, clearChildren);
   //      }
   //      else if (value instanceof Package)
   //      {
   //         treeItem = addPackageItems((Package)value, clearChildren);
   //      }
   //      else if (value instanceof FolderModel)
   //      {
   //         treeItem = addFolderItems((FolderModel)value, clearChildren);
   //      }
   //
   //      if (treeItem != null && open)
   //      {
   //         treeItem.setState(true);
   //      }
   //
   //      return treeItem;
   //   }

   private boolean childExist(TreeItem treeItem, Object childUserObject)
   {
      for (int i = 0; i < treeItem.getChildCount(); i++)
      {
         TreeItem childItem = treeItem.getChild(i);

         if (childUserObject instanceof String && childItem.getUserObject() instanceof String
            && childUserObject.equals(childItem.getUserObject()))
         {
            return true;
         }

         if (childItem.getUserObject() == childUserObject)
         {
            return true;
         }
      }

      return false;
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
            if (item.getUserObject() instanceof Project
               && ((Project)item.getUserObject()).getProject().getName().equals(parts[index]))
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
            if (item.getUserObject() instanceof FolderModel
               && ((FolderModel)item.getUserObject()).getName().equals(parts[index]))
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

   public void goToItem(List<Object> itemList, boolean collapseBranches)
   {
      /*
      TreeItem treeItem = null;
      for (int i = 0; i < itemList.size(); i++)
      {
         Object obj = itemList.get(i);
         if (obj instanceof FileModel)
         {
            if (treeItem != null)
            {
               for (int childIndex = 0; childIndex < treeItem.getChildCount(); childIndex++)
               {
                  TreeItem item = treeItem.getChild(childIndex);
                  if (item.getUserObject() instanceof FileModel
                     && ((FileModel)item.getUserObject()).getPath().equals(((FileModel)obj).getPath()))
                  {
                     tree.setSelectedItem(item);
                     return;
                  }
               }
            }
         }
         else
         {
            treeItem = openTreeNode(obj, true, collapseBranches);
            if (i == itemList.size() - 1)
            {
               tree.setSelectedItem(treeItem);
            }
         }
      }
      */
   }

}
