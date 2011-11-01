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
package org.exoplatform.ide.extension.samples.client.convert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.event.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.project.event.ConvertToProjectHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.StringProperty;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* Presenter for converting folders to projects.
* 
* After this operation project must be in the root folder.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Oct 27, 2011 4:26:33 PM anya $
 *
 */
public class ConvertToProjectPresenter implements ConvertToProjectHandler, ViewClosedHandler,
   ConfigurationReceivedSuccessfullyHandler, VfsChangedHandler
{
   interface Display extends IsView
   {
      /**
       * Get convert button click handler.
       * 
       * @return {@link HasClickHandlers} 
       */
      HasClickHandlers getConvertButton();
      
      /**
       * Get cancel button click handler.
       * 
       * @return {@link HasClickHandlers} 
       */
      HasClickHandlers getCancelButton();
      
      /**
       * Get next button click handler.
       * 
       * @return {@link HasClickHandlers} 
       */
      HasClickHandlers getNextButton();
      
      /**
       * Get back button click handler.
       * 
       * @return {@link HasClickHandlers} 
       */
      HasClickHandlers getBackButton();

      /**
       * Change enable state of next button.
       * 
       * @param enable if <code>true</code> then enabled
       */
      void enableNextButton(boolean enable);
      
      /**
       * Change enable state of back button.
       * 
       * @param enable if <code>true</code> then enabled
       */
      void enableBackButton(boolean enable);

      /**
       * Change enable state of convert button.
       * 
       * @param enable if <code>true</code> then enabled
       */
      void enableConvertButton(boolean enable);

      /**
       * Get project's type field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getProjectType();
      
      /**
       * Get project's name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getProjectName();

      /**
       * Set the list of project's types.
       * 
       * @param set types of project
       */
      void setProjectType(Set<String> set);

      /**
       * Get browser tree item.
       * 
       * @return {@link TreeGridItem}
       */
      TreeGridItem<Item> getBrowserTree();

      /**
       * Get selected items in the tree.
       * 
       * @return {@link List} selected items
       */
      List<Item> getSelectedItems();

      /**
       * Select item in browser tree by path.
       * 
       * @param itemId item's id
       */
      void selectItem(String itemId);

      /**
       * Display step one.
       */
      void stepOne();
      
      /**
       * Display step two.
       */
      void stepTwo();
   }

   private Display display;

   /**
    * Current virtual file system.
    */
   private VirtualFileSystemInfo vfs;

   /**
    * Folders to refresh.
    */
   private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   /**
    * Selected items in browser tree.
    */
   private List<Item> selectedItems = new ArrayList<Item>();

   /**
    * The id of root folder.
    */
   private String rootId;

   /**
    * Base VFS URL.
    */
   private String vfsBaseUrl;

   public ConvertToProjectPresenter()
   {
      IDE.EVENT_BUS.addHandler(ConvertToProjectEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(VfsChangedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getConvertButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            startConvert();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getNextButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            display.stepTwo();
         }
      });

      display.getBrowserTree().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            onFolderOpened((Folder)event.getTarget());
         }
      });

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         @Override
         public void onSelection(SelectionEvent<Item> event)
         {
            selectedItems = display.getSelectedItems();
            boolean enabled =
               (selectedItems != null && selectedItems.size() == 1 && !selectedItems.get(0).getId().equals(rootId));
            display.enableNextButton(enabled);
            if (enabled)
            {
               display.getProjectName().setValue(selectedItems.get(0).getName());
            }
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            display.stepOne();
         }
      });

      display.getProjectName().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean enabled = event.getValue() != null && !event.getValue().isEmpty();
            display.enableConvertButton(enabled);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.event.ConvertToProjectHandler#onConvertToProject(org.exoplatform.ide.client.framework.project.event.ConvertToProjectEvent)
    */
   @Override
   public void onConvertToProject(ConvertToProjectEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         display.enableNextButton(false);
         IDE.getInstance().openView(display.asView());
         display.setProjectType(ProjectResolver.getProjectsTypes());
         display.stepOne();
      }
      getFolders();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * Perform check item is in root folder before converting.
    */
   protected void startConvert()
   {
      String projectName = display.getProjectName().getValue();

      final Item item = selectedItems.get(0);

      //Check item in root of workspace (parent is root VFS root folder):
      if (item.getParentId().equals(vfs.getRoot().getId()))
      {
         //Check have to rename
         if (projectName.equals(item.getName()))
         {
            convert(item);
         }
         else
         {
            rename(item, projectName);
         }
      }
      else
      {
         checkNameExists(item, projectName);
      }
   }

   /**
    * Perform check root folder already contains item with given name.
    * 
    * @param item folder to convert
    * @param projectName project's name to set
    */
   protected void checkNameExists(final Item item, final String projectName)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(vfs.getRoot(),
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  for (Item child : result)
                  {
                     if (projectName.equals(child.getName()))
                     {
                        Dialogs.getInstance().showError(
                           SamplesExtension.LOCALIZATION_CONSTANT.convertNameExists(projectName));
                        return;
                     }
                  }
                  move(item);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Change name of the project.
    * 
    * @param item folder to rename
    * @param newName new name of the project
    */
   protected void rename(Item item, String newName)
   {
      try
      {
         VirtualFileSystem.getInstance().rename(item, null, newName, null,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
            {
               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  convert(result.getItem());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (Exception e)
      {
         e.printStackTrace();
         IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Move item to root folder of the VFS.
    * 
    * @param item item to move
    */
   protected void move(final Item item)
   {
      try
      {
         VirtualFileSystem.getInstance().move(item, vfs.getRoot().getId(), null,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
            {

               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  if (display.getProjectName().getValue().equals(item.getName()))
                  {
                     convert(result.getItem());
                  }
                  else
                  {
                     rename(result.getItem(), display.getProjectName().getValue());
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(e));
      }

   }

   /**
    * Convert folder to project.
    * 
    * @param item
    */
   protected void convert(final Item item)
   {
      String projectType = display.getProjectType().getValue();
      item.getProperties().add(new StringProperty("vfs:mimeType", ProjectModel.PROJECT_MIME_TYPE));
      item.getProperties().add(new StringProperty("vfs:projectType", projectType));
      try
      {
         VirtualFileSystem.getInstance().updateItem(item, null, new AsyncRequestCallback<Object>()
         {

            @Override
            protected void onSuccess(Object result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               IDE.EVENT_BUS.fireEvent(new RefreshBrowserEvent(vfs.getRoot()));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      vfsBaseUrl = event.getConfiguration().getVfsBaseUrl();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   /**
    * Get folder's content on open.
    * 
    * @param openedFolder
    */
   private void onFolderOpened(Folder openedFolder)
   {
      ItemList<Item> children =
         (openedFolder instanceof ProjectModel) ? ((ProjectModel)openedFolder).getChildren()
            : ((FolderModel)openedFolder).getChildren();

      if (!children.getItems().isEmpty())
         return;

      foldersToRefresh = new ArrayList<Folder>();
      foldersToRefresh.add(openedFolder);
      refreshNextFolder(null);
   }

   /**
    * Refresh next folder content.
    * 
    * @param itemToSelect
    */
   private void refreshNextFolder(final String itemToSelect)
   {
      if (foldersToRefresh.size() == 0)
      {
         return;
      }

      final Folder folder = foldersToRefresh.get(0);
      try
      {
         VirtualFileSystem.getInstance().getChildren(folder,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
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

                  for (Item i : result)
                  {
                     if (i instanceof ItemContext)
                     {
                        ((ItemContext)i).setParent(new FolderModel(folder));
                     }

                     if (folder instanceof ProjectModel)
                     {
                        ((ItemContext)i).setProject((ProjectModel)folder);
                     }
                     else if (folder instanceof ItemContext && ((ItemContext)folder).getProject() != null)
                     {
                        ((ItemContext)i).setProject(((ItemContext)folder).getProject());
                     }
                  }
                  folderContentReceived(folder, itemToSelect);

               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  foldersToRefresh.clear();
                  exception.printStackTrace();
               }

            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Perform actions when folder's content is received.
    * 
    * @param folder 
    * @param itemToSelect
    */
   private void folderContentReceived(Folder folder, String itemToSelect)
   {
      foldersToRefresh.remove(folder);
      List<Item> children =
         (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren().getItems() : ((FolderModel)folder)
            .getChildren().getItems();
      removeItemsNotToBeDisplayed(children);

      display.getBrowserTree().setValue(folder);

      if (foldersToRefresh.size() > 0)
      {
         refreshNextFolder(itemToSelect);
      }
      else
      {
         if (itemToSelect != null)
         {
            display.selectItem(itemToSelect);
         }
      }
   }

   private void removeItemsNotToBeDisplayed(List<Item> items)
   {
      List<Item> itemsToRemove = new ArrayList<Item>();
      for (Item item : items)
      {
         if (item.getName().startsWith(".") || !(item instanceof FolderModel))
            itemsToRemove.add(item);
      }
      items.removeAll(itemsToRemove);
   }

   /**
    * Get the list of folders in root folder.
    */
   private void getFolders()
   {
      try
      {
         String workspace = (vfsBaseUrl.endsWith("/")) ? vfsBaseUrl + vfs.getId() : vfsBaseUrl + "/" + vfs.getId();
         new VirtualFileSystem(workspace).init(new AsyncRequestCallback<VirtualFileSystemInfo>(new VFSInfoUnmarshaller(
            new VirtualFileSystemInfo()))
         {

            @Override
            protected void onSuccess(VirtualFileSystemInfo result)
            {
               rootId = result.getRoot().getId();
               display.getBrowserTree().setValue(result.getRoot());
               foldersToRefresh.add(new FolderModel(result.getRoot()));
               refreshNextFolder(result.getRoot().getId());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               foldersToRefresh.clear();
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

}
