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

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.packaging.model.Package;
import org.eclipse.jdt.client.packaging.model.Project;
import org.eclipse.jdt.client.packaging.model.ResourceDirectory;
import org.eclipse.jdt.client.packaging.model.next.JavaProject;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.FolderOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.project.api.ProjectBuilder;
import org.exoplatform.ide.client.framework.project.api.ProjectBuilder.Builder;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 *
 */
public class PackageExplorerPresenter implements ShowPackageExplorerHandler, ViewOpenedHandler, ViewClosedHandler,
   ProjectOpenedHandler, ProjectClosedHandler, SelectItemHandler,
   EditorActiveFileChangedHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
   ApplicationSettingsReceivedHandler, ItemsSelectedHandler, TreeRefreshedHandler,
   ItemLockedHandler, AddItemTreeIconHandler, RemoveItemTreeIconHandler
{

   private static final String PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG = "package-explorer-linked-with-editor";

   private static final String RECEIVE_CHILDREN_ERROR_MSG = "Service is not deployed.<br>Parent folder not found.";

   private static final String MESSAGE_LOAD_PROJECT = "Loading project structure...";

   private static final String MESSAGE_UPDATE_PROJECT = "Updating project structure...";

   private PackageExplorerDisplay display;

   private ProjectModel openedProject;

   //private Project projectItem;

   private Item selectedItem;

   private Item itemToSelect;

   //private ProjectTreeParser treeParser;

   private static PackageExplorerPresenter instance;

   private boolean linkWithEditor = false;

   private FileModel editorActiveFile;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();
   
   private Map<String, Editor> openedEditors = new HashMap<String, Editor>();

   public static PackageExplorerPresenter getInstance()
   {
      return instance;
   }

   public PackageExplorerPresenter()
   {
      instance = this;

      IDE.getInstance().addControl(new ShowPackageExplorerControl(), Docking.TOOLBAR);

      IDE.addHandler(ShowPackageExplorerEvent.TYPE, this);

      IDE.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);

      IDE.addHandler(SelectItemEvent.TYPE, this);

      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(TreeRefreshedEvent.TYPE, this);
      IDE.addHandler(ItemLockedEvent.TYPE, this);
      IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
      IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
      
      for (String type : ProjectTypes.getList())
      {
         ProjectBuilder.addBuilder(type, new Builder()
         {
            @Override
            public IDEProject build(ProjectModel project)
            {
               return new JavaProject(project);
            }
         });
      }

   }

   @Override
   public void onShowPackageExplorer(ShowPackageExplorerEvent event)
   {
      if (display == null)
      {
         if (openedProject == null)
         {
            return;
         }

         if (!ProjectTypes.contains(openedProject))
         {
            return;
         }

         display = GWT.create(PackageExplorerDisplay.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }
      else
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   private void bindDisplay()
   {
      display.getBrowserTree().addOpenHandler(new OpenHandler<Item>()
      {
         @Override
         public void onOpen(final OpenEvent<Item> event)
         {
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  FolderModel folder = (FolderModel)event.getTarget();
                  List<Item> children = display.getTreeChildren(folder);
                  IDE.fireEvent(new FolderOpenedEvent(folder, children));
               }
            });            
         }
      });
      
      display.getBrowserTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            Object selectedObject = display.getSelectedObject();
            if (selectedObject instanceof FileModel)
            {
               FileModel file = (FileModel)selectedObject;
               IDE.fireEvent(new OpenFileEvent(file));
            }
         }
      });

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         @Override
         public void onSelection(SelectionEvent<Item> event)
         {
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  treeItemSelected();
               }
            });
         }
      });

      display.getLinkWithEditorButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            linkWithEditorButtonClicked();
         }
      });

      display.setLinkWithEditorButtonSelected(linkWithEditor);
      display.setLockTokens(applicationSettings.getValueAsMap("lock-tokens"));      
   }

   private void treeItemSelected()
   {
      Object selectedObject = display.getSelectedObject();
      
      if (selectedObject instanceof Project)
      {
         selectedItem = ((Project)selectedObject).getProject();
      }
      else if (selectedObject instanceof ResourceDirectory)
      {
         selectedItem = ((ResourceDirectory)selectedObject).getFolder();
      }
      else if (selectedObject instanceof Package)
      {
         selectedItem = ((Package)selectedObject).getPackageFolder();
      }
      else if (selectedObject instanceof FolderModel)
      {
         selectedItem = (FolderModel)selectedObject;
      }
      else if (selectedObject instanceof FileModel)
      {
         selectedItem = (FileModel)selectedObject;
      }
      else
      {
         selectedItem = null;
      }

      List<Item> selectedItems = new ArrayList<Item>();
      if (selectedItem != null)
      {
         selectedItems.add(selectedItem);
      }

      changeActiveFile(selectedItem);

      IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
   }

   /**
    * Switch Editor to selected file.
    *
    * @param item
    */
   private void changeActiveFile(final Item item)
   {
      if (!linkWithEditor || item == null || !(item instanceof FileModel) || !openedFiles.containsKey(item.getId())
         || editorActiveFile.getId().equals(item.getId()))
      {
         return;
      }

      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new EditorChangeActiveFileEvent((FileModel)item));
         }
      });
   }


   @Override
   public void onSelectItem(SelectItemEvent event)
   {
      if (display == null)
      {
         return;
      }
   }

   /**
    * Handle click on "Link with Editor" button.
    */
   private void linkWithEditorButtonClicked()
   {
      linkWithEditor = !linkWithEditor;
      display.setLinkWithEditorButtonSelected(linkWithEditor);

      applicationSettings
         .setValue(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG, new Boolean(linkWithEditor), Store.COOKIES);

      //SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
      IDE.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));

      /*
       * fire event for show-hide line numbers command be able to update state.
       */
      IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));

      if (linkWithEditor && editorActiveFile != null)
      {
         goToItem(editorActiveFile, false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      editorActiveFile = event.getFile();
      if (display == null || !linkWithEditor || editorActiveFile == null)
      {
         return;
      }

      goToItem(event.getFile(), false);
   }

   /**
    * Navigate to item in the project tree.
    *
    * @param item item to navigate
    * @param collapseBranches is need to collapse tree branches
    */
   private void goToItem(final Item item, final boolean collapseBranches)
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            if (!collapseBranches)
            {
               Object selectedObject = display.getSelectedObject();
               if (selectedObject instanceof FileModel && item instanceof FileModel
                  && ((FileModel)selectedObject).getId().equals(((FileModel)item).getId()))
               {
                  return;
               }
            }

            try
            {
               display.selectItem(item);               
            }
            catch (Exception e)
            {
               e.printStackTrace();  
            }
         }
      });
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      openedEditors.remove(event.getFile());
   }

   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      openedEditors.put(event.getFile().getId(), event.getEditor());
   }

   private ApplicationSettings applicationSettings;

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsBoolean(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG) == null)
      {
         applicationSettings.setValue(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG, Boolean.FALSE, Store.COOKIES);
      }
      else
      {
         linkWithEditor = applicationSettings.getValueAsBoolean(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG);
      }
   }
   
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      openedProject = event.getProject();
      
      if (display == null)
      {
          if (ProjectTypes.contains(openedProject))
          {
             new Timer()
             {
                @Override
                public void run()
                {
                   display = GWT.create(PackageExplorerDisplay.class);
                   bindDisplay();
                   IDE.getInstance().openView(display.asView());
                }
             }.schedule(500);
          }
   
          return;         
      }

      if (!ProjectTypes.contains(openedProject))
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      display.setPackageExplorerTreeVisible(true);
      
      display.getBrowserTree().setValue(openedProject);
      //display.setProject(openedProject);
   }
   
   
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      openedProject = null;

      if (display == null)
      {
         return;
      }

      display.getBrowserTree().setValue(null);
      //display.setProject(null);
      display.setPackageExplorerTreeVisible(false);
   }
   
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof PackageExplorerDisplay && openedProject != null)
      {
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               display.setPackageExplorerTreeVisible(true);
               display.getBrowserTree().setValue(openedProject);
            }
         });
      }
   }
   
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof PackageExplorerDisplay)
      {
         display = null;
      }
   }
   
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1 && linkWithEditor)
      {
         Item selectedItem = event.getSelectedItems().get(0);
         if (editorActiveFile != null && !editorActiveFile.getId().equals(selectedItem.getId()) &&
                  (selectedItem instanceof FileModel))
         {
            IDE.fireEvent(new EditorChangeActiveFileEvent((FileModel)selectedItem));
         }
      }
   }

   @Override
   public void onTreeRefreshed(final TreeRefreshedEvent event)
   {
      if (display != null)
      {
         //System.out.println("tree refreshed. item to select >> " + itemToSelect);
         display.getBrowserTree().setValue(event.getFolder());
         if (event.getItemToSelect() != null)
         {
            display.selectItem(event.getItemToSelect());
         }
         
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               List<Item> visibleItems = display.getVisibleItems();
               IDE.fireEvent(new FolderOpenedEvent(event.getFolder(), visibleItems));
            }
         });
      }
   }

   @Override
   public void onItemLocked(ItemLockedEvent event)
   {
      Item item = event.getItem();
      if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         file.setLocked(true);
         file.setLock(new Lock("", event.getLockToken().getLockToken(), 0));
         display.updateItemState(file);
      }      
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler#onAddItemTreeIcon(org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent)
    */
   @Override
   public void onAddItemTreeIcon(AddItemTreeIconEvent event)
   {
      display.addItemsIcons(event.getTreeItemIcons());
   }   
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler#onRemoveItemTreeIcon(org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent)
    */
   @Override
   public void onRemoveItemTreeIcon(RemoveItemTreeIconEvent event)
   {
      display.removeItemIcons(event.getIconsToRemove());
   }
   
   
}
