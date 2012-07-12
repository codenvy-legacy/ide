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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.GoToFolderEvent;
import org.exoplatform.ide.client.framework.navigation.event.GoToFolderHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectHandler;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.operation.cutcopy.CopyItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.CutItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.PasteItemsEvent;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 * 
 */

public class TinyProjectExplorerPresenter implements RefreshBrowserHandler, SelectItemHandler,
   ViewVisibilityChangedHandler, ItemUnlockedHandler, ItemLockedHandler, ApplicationSettingsReceivedHandler,
   ViewClosedHandler, AddItemTreeIconHandler, RemoveItemTreeIconHandler, ShowProjectExplorerHandler,
   ItemsSelectedHandler, ViewActivatedHandler, OpenProjectHandler, VfsChangedHandler, CloseProjectHandler,
   AllFilesClosedHandler, GoToFolderHandler, EditorActiveFileChangedHandler, IDELoadCompleteHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ShowHideHiddenFilesHandler, ItemDeletedHandler
{

   private static final String DEFAULT_TITLE = "Project Explorer";

   private static final String RECEIVE_CHILDREN_ERROR_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .workspaceReceiveChildrenError();

   /**
    * Comparator for ordering projects by name.
    */
   private static final Comparator<ProjectModel> PROJECT_COMPARATOR = new ProjectComparator();

   private ProjectExplorerDisplay display;

   // private boolean viewOpened = false;

   private String itemToSelect;

   private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   private List<Item> selectedItems = new ArrayList<Item>();

   private ProjectModel openedProject;

   private FileModel editorActiveFile;

   private ApplicationSettings applicationSettings;

   private boolean ideLoadComplete = false;

   public TinyProjectExplorerPresenter()
   {
      IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);

      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ItemLockedEvent.TYPE, this);
      IDE.addHandler(ItemUnlockedEvent.TYPE, this);
      IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
      IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(OpenProjectEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(CloseProjectEvent.TYPE, this);

      IDE.addHandler(SelectItemEvent.TYPE, this);
      IDE.addHandler(GoToFolderEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
      IDE.addHandler(ItemDeletedEvent.TYPE, this);
   }

   private void ensureProjectExplorerDisplayCreated()
   {
      if (display != null)
      {
         return;
      }

      display = GWT.create(ProjectExplorerDisplay.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   public void bindDisplay()
   {
      display.getBrowserTree().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            onFolderOpened((Folder)event.getTarget());
         }
      });

      display.getBrowserTree().addCloseHandler(new CloseHandler<Item>()
      {

         public void onClose(CloseEvent<Item> event)
         {
            onCloseFolder((Folder)event.getTarget());
         }
      });

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(SelectionEvent<Item> event)
         {
            onItemSelected();
         }
      });

      display.getBrowserTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            onBrowserDoubleClicked();
         }
      });

      display.getBrowserTree().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            onKeyPressed(event.getNativeEvent().getKeyCode(), event.isControlKeyDown());
         }
      });

      display.getLinkWithEditorButton().addClickHandler(linkWithEditorButtonClickHandler);
      display.setLinkWithEditorButtonSelected(linkingWithEditor);

      display.getProjectsListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            List<ProjectModel> projectsList = display.getSelectedProjects();
            if (projectsList.size() == 1)
            {
               IDE.fireEvent(new OpenProjectEvent(projectsList.get(0)));
            }
         }
      });
   }

   /**
    * Perform actions when folder is closed in browser tree.
    * 
    * @param folder closed folder
    */
   public void onCloseFolder(Folder folder)
   {
      for (Item item : display.getSelectedItems())
      {
         if (item.getPath().startsWith(folder.getPath()) && !item.getId().equals(folder.getId()))
         {
            display.deselectItem(item.getId());
         }
      }
   }

   /**
    * 
    * Handling item selected event from browser
    * 
    * @param item
    */
   protected void onItemSelected()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            if (display == null)
            {
               return;
            }

            selectedItems = display.getSelectedItems();
            IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));

            changeActiveFileOnSelection();
         }
      });
   }

   /**
    * Handling of mouse double clicking
    */
   protected void onBrowserDoubleClicked()
   {
      if (selectedItems.size() != 1)
      {
         return;
      }

      Item item = selectedItems.get(0);

      if (item instanceof File)
      {
         IDE.fireEvent(new OpenFileEvent((FileModel)item));
      }
   }

   /**
    * Handling of folder opened event from browser
    * 
    * @param openedFolder
    */
   protected void onFolderOpened(Folder openedFolder)
   {
      // Commented to fix bug with selection of new folder
      // itemToSelect = null;
      ItemList<Item> children =
         (openedFolder instanceof ProjectModel) ? ((ProjectModel)openedFolder).getChildren()
            : ((FolderModel)openedFolder).getChildren();
      if (!children.getItems().isEmpty())
      {
         return;
      }

      foldersToRefresh.clear();
      foldersToRefresh.add(openedFolder);
      display.setUpdateTreeValue(false);
      refreshNextFolder();
   }

   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      if (display == null)
      {
         return;
      }

      if (!display.asView().isActive() && !"ideTinyProjectExplorerView".equals(lastNavigatorId))
      {
         return;
      }

      if (event.getItemToSelect() != null)
      {
         itemToSelect = event.getItemToSelect().getId();
      }
      else
      {
         List<Item> selectedItems = display.getSelectedItems();
         if (selectedItems.size() > 0)
         {
            itemToSelect = selectedItems.get(0).getId();
         }
         else
         {
            itemToSelect = null;
         }
      }

      if (event.getFolders() != null)
      {
         foldersToRefresh = event.getFolders();
      }
      else
      {
         foldersToRefresh = new ArrayList<Folder>();

         if (selectedItems.size() > 0)
         {
            Item item = selectedItems.get(0);
            if (item instanceof FileModel)
            {
               foldersToRefresh.add(((FileModel)item).getParent());
            }
            else if (item instanceof Folder)
            {
               foldersToRefresh.add((Folder)item);
            }
         }
      }
      display.setUpdateTreeValue(false);
      refreshNextFolder();
   }

   /**
    * Refresh folder's properties.
    * 
    * @param folder
    */
   private void refreshFolderProperties(final Folder folder)
   {
      try
      {
         VirtualFileSystem.getInstance().getItemById(folder.getId(),
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
            {

               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  folder.getProperties().clear();
                  folder.getProperties().addAll(result.getItem().getProperties());
               }

               protected void onFailure(Throwable exception)
               {
               }
            });
      }
      catch (RequestException e)
      {
      }
   }

   private void refreshNextFolder()
   {
      if (foldersToRefresh.size() == 0)
      {
         if (itemToSelect != null)
         {
            display.selectItem(itemToSelect);
            itemToSelect = null;
         }

         return;
      }

      final Folder folder = foldersToRefresh.get(0);
      //remove folder hear to open sever folder simultaneously
      foldersToRefresh.remove(folder);
      refreshFolderProperties(folder);
      try
      {
         display.changeFolderIcon(folder, true);
         VirtualFileSystem.getInstance().getChildren(folder,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onFailure(Throwable exception)
               {
                  itemToSelect = null;
                  foldersToRefresh.clear();
                  IDE.fireEvent(new ExceptionThrownEvent(exception, RECEIVE_CHILDREN_ERROR_MSG));
                  IDE.fireEvent(new EnableStandartErrorsHandlingEvent());
               }

               @Override
               protected void onSuccess(List<Item> result)
               {
                  folderContentReceived(folder, result);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void folderContentReceived(Folder folder, List<Item> result)
   {
      //      loader.hide();
      for (Item i : result)
      {
         if (i instanceof ItemContext)
         {
            ItemContext contect = (ItemContext)i;
            contect.setParent(new FolderModel(folder));
            contect.setProject(openedProject);
         }
      }

      if (folder instanceof FolderModel)
      {
         ((FolderModel)folder).getChildren().getItems().clear();
         ((FolderModel)folder).getChildren().getItems().addAll(result);
      }
      else if (folder instanceof ProjectModel)
      {
         ((ProjectModel)folder).getChildren().getItems().clear();
         ((ProjectModel)folder).getChildren().getItems().addAll(result);
      }

      IDE.fireEvent(new FolderRefreshedEvent(folder));

      // TODO if will be some value - display system items or not, then add check here:
      List<Item> children =
         (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren().getItems() : ((FolderModel)folder)
            .getChildren().getItems();
      // removeSystemItems(children);
      Collections.sort(children, comparator);

      display.getBrowserTree().setValue(folder);
      display.changeFolderIcon(folder, false);
      //display.asView().setViewVisible();

      refreshNextFolder();
   }

   /**
    * Comparator for comparing items in received directory.
    */
   private Comparator<Item> comparator = new Comparator<Item>()
   {
      public int compare(Item item1, Item item2)
      {
         if (item1 instanceof Folder && item2 instanceof FileModel)
         {
            return -1;
         }
         else if (item1 instanceof File && item2 instanceof Folder)
         {
            return 1;
         }
         return item1.getName().compareTo(item2.getName());
      }
   };

   /**
    * Select chosen item in browser.
    * 
    * @see org.exoplatform.ide.client.browser.event.SelectItemHandler#onSelectItem(org.exoplatform.ide.client.browser.event.SelectItemEvent)
    */
   public void onSelectItem(SelectItemEvent event)
   {
      if (display == null)
      {
         return;
      }

      display.selectItem(event.getItemHref());
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      Item item = event.getItem();
      onRefreshBrowser(new RefreshBrowserEvent());
      if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         file.setLocked(false);
         file.setLock(null);
         display.updateItemState(file);
      }
   }

   public void onItemLocked(ItemLockedEvent event)
   {
      Item item = event.getItem();
      onRefreshBrowser(new RefreshBrowserEvent());
      if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         file.setLocked(true);
         file.setLock(new Lock("", event.getLockToken().getLockToken(), 0));
         display.updateItemState(file);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      if (applicationSettings.getValueAsBoolean("project-explorer-linked-with-editor") == null)
      {
         applicationSettings.setValue("project-explorer-linked-with-editor", Boolean.FALSE, Store.COOKIES);
      }
      linkingWithEditor = applicationSettings.getValueAsBoolean("project-explorer-linked-with-editor");

      ensureProjectExplorerDisplayCreated();

      display.setLockTokens(applicationSettings.getValueAsMap("lock-tokens"));
      display.setLinkWithEditorButtonSelected(linkingWithEditor);

      if (openedProject == null)
      {
         display.setLinkWithEditorButtonEnabled(false);
      }
   }

   // keyboard keys doesn't work within the TreeGrid in the Internet Explorer 8.0, Safari 5.0.2 and Google Chrome 7.0.5
   // seems because of SmartGWT issues
   protected void onKeyPressed(int keyCode, boolean isControlKeyDown)
   {
      if (isControlKeyDown)
      {
         // "Ctrl+C" hotkey handling
         if (String.valueOf(keyCode).toUpperCase().equals("C"))
         {
            IDE.fireEvent(new CopyItemsEvent());
         }

         // "Ctrl+X" hotkey handling
         else if (String.valueOf(keyCode).toUpperCase().equals("X"))
         {
            IDE.fireEvent(new CutItemsEvent());
         }

         // "Ctrl+V" hotkey handling
         else if (String.valueOf(keyCode).toUpperCase().equals("V"))
         {
            IDE.fireEvent(new PasteItemsEvent());
         }
      }

      // "Delete" hotkey handling
      else if (keyCode == KeyCodes.KEY_DELETE)
      {
         IDE.fireEvent(new DeleteItemEvent());
      }

      // "Enter" hotkey handling - impossible to handle Enter key pressing event within the TreeGrid and ListGrid in the
      // SmartGWT 2.2 because of bug when Enter keypress is not caugth.
      // http://code.google.com/p/smartgwt/issues/detail?id=430
      // else if (charCode == KeyCodes.KEY_ENTER)
      // {
      // onBrowserDoubleClicked();
      // }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof ProjectExplorerDisplay && event.getView().isViewVisible())
      {
         onItemSelected();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler#onRemoveItemTreeIcon(org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent)
    */
   @Override
   public void onRemoveItemTreeIcon(RemoveItemTreeIconEvent event)
   {
      display.removeItemIcons(event.getIconsToRemove());
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler#onAddItemTreeIcon(org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent)
    */
   @Override
   public void onAddItemTreeIcon(AddItemTreeIconEvent event)
   {
      display.addItemsIcons(event.getTreeItemIcons());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof ProjectExplorerDisplay)
      {
         display = null;
      }
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
   }

   private String lastNavigatorId = null;

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      if ("ideWorkspaceView".equals(event.getView().getId()) && !event.getView().getId().equals(lastNavigatorId))
      {
         lastNavigatorId = "ideWorkspaceView";
      }
      else if ("ideTinyProjectExplorerView".equals(event.getView().getId())
         && !event.getView().getId().equals(lastNavigatorId))
      {
         lastNavigatorId = "ideTinyProjectExplorerView";
      }

      if (event.getView() instanceof ProjectExplorerDisplay)
      {
         onItemSelected();
      }
   }

   private void loadProject()
   {
      display.setProjectExplorerTreeVisible(true);
      display.getBrowserTree().setValue(null);
      display.getBrowserTree().setValue(openedProject);
      display.asView().setTitle(openedProject.getName());
      display.selectItem(openedProject.getId());
      selectedItems = display.getSelectedItems();

      IDE.fireEvent(new ProjectOpenedEvent(openedProject));

      display.setLinkWithEditorButtonEnabled(true);
      display.setLinkWithEditorButtonSelected(linkingWithEditor);

      // Folder folder = (Folder)navigatorSelectedItems.get(0);
      foldersToRefresh.clear();
      foldersToRefresh.add(openedProject);
      refreshNextFolder();
   }

   @Override
   public void onOpenProject(OpenProjectEvent event)
   {
      ensureProjectExplorerDisplayCreated();

      if (openedProject != null)
      {
         if (openedProject.getId().equals(event.getProject().getId())
            && openedProject.getName().equals(event.getProject().getName()))
         {
            return;
         }
      }

      openedProject = new ProjectModel(event.getProject());
      loadProject();
   }

   @Override
   public void onShowProjectExplorer(ShowProjectExplorerEvent event)
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      ensureProjectExplorerDisplayCreated();

      if (openedProject == null)
      {
         display.setProjectExplorerTreeVisible(false);
         refreshProjectsList();
      }
      else
      {
         loadProject();
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      if (display == null)
      {
         return;
      }

      display.setProjectExplorerTreeVisible(false);
      if (ideLoadComplete)
      {
         refreshProjectsList();
      }

      display.asView().setTitle(DEFAULT_TITLE);
   }

   @Override
   public void onCloseProject(CloseProjectEvent event)
   {
      if (openedProject == null)
      {
         return;
      }

      IDE.addHandler(AllFilesClosedEvent.TYPE, this);
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new CloseAllFilesEvent());
         }
      });
   }

   @Override
   public void onAllFilesClosed(AllFilesClosedEvent event)
   {
      IDE.removeHandler(AllFilesClosedEvent.TYPE, this);

      if (openedProject == null)
      {
         return;
      }

      final ProjectClosedEvent projectClosedEvent = new ProjectClosedEvent(openedProject);
      openedProject = null;
      if (display != null)
      {
         display.getBrowserTree().setValue(null);
         display.asView().setTitle(DEFAULT_TITLE);
         display.setProjectExplorerTreeVisible(false);
         refreshProjectsList();
         display.setLinkWithEditorButtonEnabled(false);
      }

      selectedItems.clear();
      IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));

      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(projectClosedEvent);
         }
      });
   }

   @Override
   public void onGoToFolder(GoToFolderEvent event)
   {
      goToFolder();
   }

   private void goToFolder()
   {
      if (display == null || openedProject == null || editorActiveFile == null)
      {
         return;
      }

      if (!editorActiveFile.getPath().startsWith(openedProject.getPath()))
      {
         return;
      }

      if (!display.asView().isViewVisible())
      {
         display.asView().activate();
      }

      if (display.selectItem(editorActiveFile.getId()))
      {
         return;
      }

      //      // If project explorer is not visible then display.selectItem() finds item in tree but does not selects it.
      //      if (display.asView().isViewVisible())
      //      {
      //         if (display.selectItem(editorActiveFile.getId()))
      //         {
      //            return;
      //         }
      //      }
      //      else
      //      {
      //         // First we need activate project explorer because
      //         // code below do not select item in tree.
      //         display.asView().activate();
      //      }

      // If we do not find item in tree then try to find item in VFS.
      String expandPath = editorActiveFile.getPath().substring(openedProject.getPath().length());
      itemsToBeOpened.clear();
      itemsToBeOpened.add(openedProject.getPath());

      String[] parts = expandPath.split("/");
      String work = openedProject.getPath();

      for (int i = 0; i < parts.length; i++)
      {
         String part = parts[i];
         if ("".equals(part))
         {
            continue;
         }

         work += "/" + part;
         itemsToBeOpened.add(work);
      }

      foldersToRefresh.clear();
      cyclicallyCheckItemsToBeRefreshed();
   }

   private List<String> itemsToBeOpened = new ArrayList<String>();

   private void cyclicallyCheckItemsToBeRefreshed()
   {
      if (itemsToBeOpened.size() == 0)
      {
         if (foldersToRefresh.size() > 0 || itemToSelect != null)
         {
            display.setUpdateTreeValue(true);
            refreshNextFolder();
         }

         return;
      }

      String path = itemsToBeOpened.get(0);
      try
      {
         VirtualFileSystem.getInstance().getItemByPath(path,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FileModel())))
            {
               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  itemsToBeOpened.remove(0);

                  if (result.getItem() instanceof ProjectModel)
                  {
                     foldersToRefresh.add((ProjectModel)result.getItem());
                  }
                  else if (result.getItem() instanceof FolderModel)
                  {
                     foldersToRefresh.add((FolderModel)result.getItem());
                  }
                  else if (result.getItem() instanceof FileModel)
                  {
                     FileModel file = (FileModel)result.getItem();
                     if (file instanceof ItemContext && openedProject != null)
                     {
                        ((ItemContext)file).setProject(openedProject);
                     }

                     itemToSelect = result.getItem().getId();
                  }

                  cyclicallyCheckItemsToBeRefreshed();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  itemsToBeOpened.clear();
                  foldersToRefresh.clear();
                  itemToSelect = null;

                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });

      }
      catch (Exception e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /*
    * Linking With Editor functionality
    */

   /**
    * Enabled or disabled Linking with Editor.
    */
   private boolean linkingWithEditor = false;

   /**
    * Opened files.
    */
   private Map<String, FileModel> openedFiles;

   /**
    * Link with Editor button click handler.
    */
   private ClickHandler linkWithEditorButtonClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         linkingWithEditor = !linkingWithEditor;
         display.setLinkWithEditorButtonSelected(linkingWithEditor);

         applicationSettings.setValue("project-explorer-linked-with-editor", new Boolean(linkingWithEditor),
            Store.COOKIES);

         SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
         /*
          * fire event for show-hide line numbers command be able to update state.
          */
         IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));

         if (linkingWithEditor)
         {
            goToFolder();
         }
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      editorActiveFile = event.getFile();

      if (ideLoadComplete && linkingWithEditor)
      {
         if (selectedItems.size() == 1 && editorActiveFile != null
            && editorActiveFile.getId().equals(selectedItems.get(0).getId()))
         {
            return;
         }

         goToFolder();
      }
   }

   /**
    * Changes the active file in Editor just after item selected in the Tree.
    */
   private void changeActiveFileOnSelection()
   {
      if (!ideLoadComplete || !linkingWithEditor || selectedItems.size() != 1)
      {
         return;
      }

      Item selectedItem = selectedItems.get(0);
      FileModel file = openedFiles.get(selectedItem.getId());
      if (file != null && editorActiveFile != null)
      {
         if (!file.getId().equals(editorActiveFile.getId()))
         {
            IDE.fireEvent(new EditorChangeActiveFileEvent(file));
         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler#onIDELoadComplete(org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent)
    */
   @Override
   public void onIDELoadComplete(IDELoadCompleteEvent event)
   {
      ideLoadComplete = true;

      if (linkingWithEditor)
      {
         goToFolder();
      }

      if (openedProject == null)
      {
         refreshProjectsList();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent)
    */
   @Override
   public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event)
   {
      if (display != null && openedProject != null)
      {
         foldersToRefresh.clear();
         foldersToRefresh.add(openedProject);
         refreshNextFolder();
      }
   }

   /**
    * Refreshes the list of existing projects in project explorer and changes visibility of projects list.
    */
   private void refreshProjectsList()
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
            ItemType.PROJECT, new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  if (openedProject != null)
                  {
                     display.setProjectsListGridVisible(false);
                     display.setProjectNotOpenedPanelVisible(false);
                     return;
                  }

                  List<ProjectModel> projects = new ArrayList<ProjectModel>();
                  for (Item item : result)
                  {
                     if (item instanceof ProjectModel)
                     {
                        projects.add((ProjectModel)item);
                     }
                  }

                  Collections.sort(projects, PROJECT_COMPARATOR);
                  display.getProjectsListGrid().setValue(projects);

                  if (projects.size() == 0)
                  {
                     display.setProjectsListGridVisible(false);
                     display.setProjectNotOpenedPanelVisible(true);
                  }
                  else
                  {
                     display.setProjectsListGridVisible(true);
                     display.setProjectNotOpenedPanelVisible(false);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, "Searching of projects failed."));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent)
    */
   @Override
   public void onItemDeleted(ItemDeletedEvent event)
   {
      if (event.getItem() instanceof ProjectModel)
      {
         if (openedProject == null)
         {
            refreshProjectsList();
         }
      }
   }

}
