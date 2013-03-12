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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Grid;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;

import org.eclipse.jdt.client.packaging.model.next.JavaProject;
import org.eclipse.jdt.client.packaging.model.next.Package;
import org.eclipse.jdt.client.packaging.model.next.SourceDirectory;
import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.ui.ProjectTreeItem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
   
   private Map<String, String> locktokens = new HashMap<String, String>();   
   
   /**
    * Comparator for comparing items in received directory.
    */
   private Comparator<Item> comparator = new Comparator<Item>()
   {
      public int compare(Item item1, Item item2)
      {
         if (item1 instanceof FolderModel && item2 instanceof FileModel)
         {
            return -1;
         }
         else if (item1 instanceof FileModel && item2 instanceof FolderModel)
         {
            return 1;
         }
         return item1.getName().compareTo(item2.getName());
      }
   };
     

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
      if (tree.getSelectedItem() == null)
      {
         return null;
      }
      
      return tree.getSelectedItem().getUserObject();
   }

   public boolean selectItem(Item item)
   {
      refreshProject(project, item);

      PackageExplorerTreeItem treeItem = treeItems.get(item.getId());
      if (treeItem == null)
      {
         updateHighlighter(null);
         return false;
      }

      if (treeItem.getParentItem() == null && !item.getId().equals(project.getId()))
      {
         updateHighlighter(null);
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

      updateHighlighter(treeItem);
      return true;
   }
   
   private void updateHighlighter(final TreeItem treeItem)
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            if (treeItem != null)
            {
               moveHighlight(treeItem);
            }
            else
            {
               hideHighlighter();
            }            
         }
      });      
   }
   
   private void refreshProject(JavaProject project, Item destinationItem)
   {
      /*
       * Refresh java project tree node
       */
      PackageExplorerTreeItem treeItem = treeItems.get(project.getId());
      treeItem.setUserObject(project);
      treeItem.setState(true);
      treeItem.render();

      /*
       * Remove nonexistent tree items.
       */
      ArrayList<String> projectItems = new ArrayList<String>();
      
      for (SourceDirectory sourceDirectory : project.getSourceDirectories())
      {
         projectItems.add(sourceDirectory.getId());
      }
      
      for (Item projectItem : project.getChildren().getItems())
      {
         if (DirectoryFilter.get().matchWithPattern(projectItem.getName()))
         {
            continue;
         }
         
         if (projectItem instanceof FileModel)
         {
            projectItems.add(projectItem.getId());
         }
         else if (projectItem instanceof FolderModel)
         {
            if (isSource(projectItem, project))
            {
               continue;
            }
            
            projectItems.add(projectItem.getId());
         }
      }
      
      ArrayList<TreeItem> itemsToRemove = new ArrayList<TreeItem>();
      for (int i = 0; i < treeItem.getChildCount(); i++)
      {
         TreeItem child = treeItem.getChild(i);
         if (!(child instanceof PackageExplorerTreeItem))
         {
            itemsToRemove.add(child);
            continue;
         }
         
         PackageExplorerTreeItem childItem = (PackageExplorerTreeItem)child;
         if (!projectItems.contains( ((Item)childItem.getUserObject()).getId() ))
         {
            itemsToRemove.add(child);
         }         
      }

      for (TreeItem child : itemsToRemove)
      {
         treeItem.removeItem(child);
      }
      
      /*
       * Add missing tree items.
       */
      int index = 0;
      
      for (SourceDirectory sourceDirectory : project.getSourceDirectories())
      {
         PackageExplorerTreeItem child = treeItem.getChildByItemId(sourceDirectory.getId());
         if (child == null)
         {
            child = new PackageExplorerTreeItem(sourceDirectory, locktokens);
            treeItem.insertItem(index, child);
            treeItems.put(sourceDirectory.getId(), child);
         }
         else
         {
            child.setUserObject(sourceDirectory);
            child.render();
         }
         index++;
      }
      
//      ArrayList<Item> projectChildren = new ArrayList<Item>();
//      projectChildren.addAll(project.getChildren().getItems());
//      Collections.sort(projectChildren, comparator);

      Collections.sort(project.getChildren().getItems(), comparator);
      
      for (Item projectItem : project.getChildren().getItems())
//      for (Item projectItem : projectChildren)
      {
         if (DirectoryFilter.get().matchWithPattern(projectItem.getName()))
         {
            continue;
         }
         
         if (projectItem instanceof FolderModel && isSource(projectItem, project))
         {
            continue;
         }
         
         PackageExplorerTreeItem child = treeItem.getChildByItemId(projectItem.getId());
         if (child == null)
         {
            child = new PackageExplorerTreeItem(projectItem, locktokens);
            treeItem.insertItem(index, child);
            treeItems.put(projectItem.getId(), child);
         }
         else
         {
            child.setUserObject(projectItem);
            child.render();
         }
         index++;
      }
      
      for (ProjectModel module : project.getModules())
      {
         if (destinationItem.getPath().startsWith(module.getPath()))
         {
            if (module instanceof JavaProject)
            {
               refreshProject((JavaProject)module, destinationItem);
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
         if (destinationItem.getPath().startsWith(sourceDirectory.getPath()))
         {
            refreshSourceDirectory(sourceDirectory, destinationItem);
            return;
         }
      }

      for (Item item : project.getChildren().getItems())
      {
         if (!(item instanceof FolderModel))
         {
            continue;
         }
         
         FolderModel folder = (FolderModel)item;
         if (isSource(folder, project))
         {
            continue;
         }
         
         if (destinationItem.getPath().startsWith(folder.getPath()))
         {
            refreshFolder((FolderModel)item, project, destinationItem);
            return;
         }
      }
   }

   private void refreshSourceDirectory(SourceDirectory sourceDirectory, Item item)
   {
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
               child = new PackageExplorerTreeItem(_package, locktokens);
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
      
      // TODO
      Collections.sort(defaultPackage.getFiles(), comparator);
      
      for (FileModel file : defaultPackage.getFiles())
      {
         PackageExplorerTreeItem child = treeItem.getChildByItemId(file.getId());
         if (child == null)
         {
            child = new PackageExplorerTreeItem(file, locktokens);
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
            try
            {
               if (_package.getPath().equals(((FileModel)item).getParent().getPath()))
               {
                  refreshPackage(_package, item);
                  return;
               }
            }
            catch (NullPointerException e)
            {
               e.printStackTrace();
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
            child = new PackageExplorerTreeItem(file, locktokens);
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
   
   private void refreshFolder(FolderModel folder, JavaProject project, Item destinationItem)
   {
      /*
       * Refresh folder tree node
       */
      PackageExplorerTreeItem treeItem = treeItems.get(folder.getId());
      treeItem.setUserObject(folder);
      treeItem.setState(true);
      treeItem.render();

      /*
       * Remove nonexistent tree items.
       */
      ArrayList<String> folderItems = new ArrayList<String>();
      for (Item item : folder.getChildren().getItems())
      {
         if (DirectoryFilter.get().matchWithPattern(item.getName()))
         {
            continue;
         }
         
         if (item instanceof FileModel)
         {
            folderItems.add(item.getId());
         }
         else if (item instanceof FolderModel)
         {
            if (isSource(item, project))
            {
               continue;
            }
            
            folderItems.add(item.getId());
         }         
      }

      ArrayList<TreeItem> itemsToRemove = new ArrayList<TreeItem>();
      for (int i = 0; i < treeItem.getChildCount(); i++)
      {
         TreeItem child = treeItem.getChild(i);
         if (!(child instanceof PackageExplorerTreeItem))
         {
            itemsToRemove.add(child);
            continue;
         }
         
         PackageExplorerTreeItem childItem = (PackageExplorerTreeItem)child;
         if (!folderItems.contains( ((Item)childItem.getUserObject()).getId() ))
         {
            itemsToRemove.add(child);
         }         
      }

      for (TreeItem child : itemsToRemove)
      {
         treeItem.removeItem(child);
      }
      
      /*
       * Add missing tree items.
       */
      Collections.sort(folder.getChildren().getItems(), comparator);
      
      int index = 0;
      for (Item folderItem : folder.getChildren().getItems())
      {
         if (DirectoryFilter.get().matchWithPattern(folderItem.getName()))
         {
            continue;
         }

         if (folderItem instanceof FolderModel && isSource(folderItem, project))
         {
            continue;
         }
         
         PackageExplorerTreeItem child = treeItem.getChildByItemId(folderItem.getId());
         if (child == null)
         {
            child = new PackageExplorerTreeItem(folderItem, locktokens);
            treeItem.insertItem(index, child);
            treeItems.put(folderItem.getId(), child);
         }
         else
         {
            child.setUserObject(folderItem);
            child.render();
         }
         index++;
      }
      
      for (Item item : folder.getChildren().getItems())
      {
         if (!(item instanceof FolderModel))
         {
            continue;
         }
         
         if (isSource(item, project))
         {
            continue;
         }
         
         if (destinationItem.getPath().startsWith(item.getPath()))
         {
            refreshFolder((FolderModel)item, project, destinationItem);
            return;
         }
      }
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
         PackageExplorerTreeItem rootTreeItem = new PackageExplorerTreeItem(value, locktokens);
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

      treeItem.setState(true, false);
   }

   private void openJavaProject(JavaProject javaProject, PackageExplorerTreeItem projectItem)
   {
      projectItem.removeItems();

      // modules
      for (ProjectModel module : javaProject.getModules())
      {
         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(module, locktokens);
         projectItem.addItem(treeItem);
         treeItems.put(module.getId(), treeItem);
         treeItem.addItem("");
      }

      // source directories
      for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories())
      {
         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(sourceDirectory, locktokens);
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
      
      Collections.sort(javaProject.getChildren().getItems(), comparator);
      
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
         
         if (DirectoryFilter.get().matchWithPattern(item.getName()))
         {
            continue;
         }

         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(item, locktokens);
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

         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(pack, locktokens);
         sourceDirectoryTreeItem.addItem(treeItem);
         treeItems.put(pack.getId(), treeItem);

         if (!pack.getFiles().isEmpty())
         {
            treeItem.addItem("");
         }
      }

      if (defaultPackage != null)
      {
         Collections.sort(defaultPackage.getFiles(), comparator);
         for (FileModel file : defaultPackage.getFiles())
         {
            PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(file, locktokens);
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

   private void openPackage(org.eclipse.jdt.client.packaging.model.next.Package pack, PackageExplorerTreeItem packageTreeItem)
   {
      packageTreeItem.removeItems();

      Collections.sort(pack.getFiles(), comparator);
      for (FileModel file : pack.getFiles())
      {
         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(file, locktokens);
         packageTreeItem.addItem(treeItem);
         treeItems.put(file.getId(), treeItem);
      }
   }

   private void openProjectFolder(FolderModel folder, PackageExplorerTreeItem folderTreeItem)
   {
      folderTreeItem.removeItems();

      Collections.sort(folder.getChildren().getItems(), comparator);
      
      for (Item item : folder.getChildren().getItems())
      {
         if (isSource(item, folder.getProject()))
         {
            continue;
         }

         PackageExplorerTreeItem treeItem = new PackageExplorerTreeItem(item, locktokens);
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
   
   /**
   * @param file
   */
   public void updateFileState(FileModel file)
   {
      PackageExplorerTreeItem item = treeItems.get(file.getId());
      if (item == null)
      {
         return;
      }
      
      item.render();
   } 

   /**
   * Set lock token map
   * 
   * @param lockTokens
   */
   public void setLockTokens(Map<String, String> lockTokens)
   {
      this.locktokens.clear();
      
      if (locktokens != null)
      {
         this.locktokens.putAll(lockTokens);
      }      
   }
   
   /**
   * Add info icons to Item main icon
   * 
   * @param itemsIcons Map of Item, info icon position and info icon URL
   */
   public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons)
   {
      for (Item item : itemsIcons.keySet())
      {
         TreeItem node = treeItems.get(item.getId());
         if (node == null)
         {
            continue;
         }
         Grid grid = (Grid)node.getWidget();
         TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
         Map<TreeIconPosition, ImageResource> map = itemsIcons.get(item);
         for (TreeIconPosition position : map.keySet())
         {
            treeIcon.addIcon(position, map.get(position));
         }   
      }
   }   
   
   /**
   * Remove info icon from Item main icon
   * 
   * @param itemsIcons Map of item and position of info icon
   */
   public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons)
   {
      for (Item item : itemsIcons.keySet())
      {
         TreeItem node = treeItems.get(item.getId());
         if (node == null)
         {
            continue;
         }
         
         Grid grid = (Grid)node.getWidget();
         TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
         treeIcon.removeIcon(itemsIcons.get(item));
      }
   }
   
   List<Item> getTreeChildren(FolderModel folder)
   {
      List<Item> children = new ArrayList<Item>();
      
      PackageExplorerTreeItem treeItem = treeItems.get(folder.getId());
      if (treeItem != null)
      {
         for (int i = 0; i < treeItem.getChildCount(); i++)
         {
            TreeItem child = treeItem.getChild(i);
            if (!(child instanceof PackageExplorerTreeItem))
            {
               continue;
            }
            
            Item childItem = (Item)child.getUserObject();
            if (childItem instanceof FolderModel || childItem instanceof FileModel)
            {
               children.add(childItem);
            }
         }
      }
      
      return children;
   }
   
   public List<Item> getVisibleItems()
   {
      List<Item> visibleItems = new ArrayList<Item>();
      if (project != null)
      {
         PackageExplorerTreeItem projectItem = treeItems.get(project.getId());
         visibleItems.add((Item)projectItem.getUserObject());
         visibleItems.addAll(getVisibleItems(projectItem));
      }
      
      return visibleItems;
   }
   
   private List<Item> getVisibleItems(PackageExplorerTreeItem treeItem)
   {
      List<Item> visibleItems = new ArrayList<Item>();
      if (treeItem.getState())
      {
         for (int i = 0; i < treeItem.getChildCount(); i++)
         {
            TreeItem child = treeItem.getChild(i);
            if (!(child instanceof PackageExplorerTreeItem))
            {
               continue;
            }

            Item item = (Item)child.getUserObject();
            if (!(item instanceof FileModel || item instanceof FolderModel))
            {
               continue;
            }
            
            visibleItems.add(item);
            
            if (item instanceof FolderModel && child.getState())
            {
               visibleItems.addAll( getVisibleItems((PackageExplorerTreeItem)child) );
            }
         }
      }
      
      return visibleItems;
   }
   
   public void refresh()
   {
      System.out.println("PEItemTree.refresh()");
   }
   
}
