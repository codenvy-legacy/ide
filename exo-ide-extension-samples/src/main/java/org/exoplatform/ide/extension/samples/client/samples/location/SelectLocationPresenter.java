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
package org.exoplatform.ide.extension.samples.client.samples.location;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.extension.samples.client.samples.load.ShowSamplesEvent;
import org.exoplatform.ide.extension.samples.client.wizard.location.WizardLocationStepPresenter;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

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

/**
 * TODO: this presenter duplicates some functionality from
 * {@link WizardLocationStepPresenter}.
 * In future this must be fix.
 * 
 * Presenter to show navigation tree, where user will be able to select location
 * for importing sample application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SelectLocationPresenter.java Aug 31, 2011 12:03:46 PM vereshchaka $
 */
public class SelectLocationPresenter implements SelectLocationHandler, ViewClosedHandler, VfsChangedHandler,
   ConfigurationReceivedSuccessfullyHandler
{
   public interface Display extends IsView
   {
      /**
       * @return {@link TreeGridItem}
       */
      TreeGridItem<Item> getNavigationTree();

      /**
       * Get selected items in the tree.
       * 
       * @return {@link List} selected items
       */
      List<Item> getSelectedItems();

      /**
       * Select item in navigation tree by path.
       * 
       * @param itemId item's path
       */
      void selectItem(String itemId);

      HasValue<String> getFolderNameField();

      HasClickHandlers getNewFolderButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getFinishButton();

      HasClickHandlers getBackButton();

      void enableFinishButton(boolean enable);

      void enableNewFolderButton(boolean enable);

      void focusInFolderNameField();
   }

   private static final SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;

   private Display display;

   /**
    * Current virtual file system.
    */
   private VirtualFileSystemInfo vfs;

   private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   private List<Item> selectedItems = new ArrayList<Item>();

   /**
    * The if of root folder.
    */
   private String rootId;

   /**
    * Repository, that was selected on previous step.
    * In this step it will be cloned.
    */
   private Repository repository;

   private String vfsBaseUrl;

   public SelectLocationPresenter()
   {
      IDE.addHandler(SelectLocationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getNavigationTree().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            onFolderOpened((Folder)event.getTarget());
         }
      });

      display.getNavigationTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         @Override
         public void onSelection(SelectionEvent<Item> event)
         {
            selectedItems = display.getSelectedItems();
            if (selectedItems == null || selectedItems.isEmpty())
            {
               display.enableNewFolderButton(false);
               display.enableFinishButton(false);
            }
            else
            {
               display.focusInFolderNameField();
               display.enableFinishButton(!selectedItems.get(0).getId().equals(rootId));
               final String folderName = display.getFolderNameField().getValue();
               display.enableNewFolderButton(folderName != null && !folderName.isEmpty());
            }
         }
      });

      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            cloneRepository(repository);
            closeView();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ShowSamplesEvent());
            closeView();
         }
      });

      display.getNewFolderButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            createFolder();
         }
      });

      display.getFolderNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            final String name = display.getFolderNameField().getValue();
            if (name == null || name.isEmpty() || selectedItems == null || selectedItems.isEmpty())
            {
               display.enableNewFolderButton(false);
            }
            else
            {
               display.enableNewFolderButton(true);
            }
         }
      });

      display.enableNewFolderButton(false);

      getFolders();
   }

   /**
    * @see org.exoplatform.ide.client.welcome.selectlocation.SelectLocationHandler#onSelectLocation(org.exoplatform.ide.client.welcome.selectlocation.SelectLocationEvent)
    */
   @Override
   public void onSelectLocation(SelectLocationEvent event)
   {
      repository = event.getRepository();
      openView();
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
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

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

   private void folderContentReceived(Folder folder, String itemToSelect)
   {
      foldersToRefresh.remove(folder);
      List<Item> children =
         (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren().getItems() : ((FolderModel)folder)
            .getChildren().getItems();
      removeItemsNotToBeDisplayed(children);

      display.getNavigationTree().setValue(folder);

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
               display.getNavigationTree().setValue(result.getRoot());
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

   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         display = d;
         bindDisplay();
         return;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Select Location View must be null"));
      }
   }

   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   private void createFolder()
   {
      if (selectedItems == null || selectedItems.isEmpty())
      {
         IDE.fireEvent(new ExceptionThrownEvent(lb.selectLocationErrorParentFolderNotSelected()));
         return;
      }

      final String newFolderName = display.getFolderNameField().getValue();
      if (newFolderName == null || newFolderName.isEmpty())
      {
         IDE.fireEvent(new ExceptionThrownEvent(lb.selectLocationErrorFolderNameEmpty()));
         return;
      }
      final FolderModel baseFolder = (FolderModel)selectedItems.get(0);
      FolderModel newFolder = new FolderModel();
      newFolder.setName(newFolderName);
      try
      {
         VirtualFileSystem.getInstance().createFolder(baseFolder,
            new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(newFolder))
            {
               @Override
               protected void onSuccess(FolderModel result)
               {
                  foldersToRefresh.clear();
                  foldersToRefresh.add(baseFolder);
                  display.getFolderNameField().setValue("");
                  refreshNextFolder(result.getId());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, lb.selectLocationErrorCantCreateFolder()));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.fireEvent(new ExceptionThrownEvent(e, lb.selectLocationErrorCantCreateFolder()));
      }
   }

   private void cloneRepository(Repository repo)
   {
      //TODO
      ProjectModel projectId = ((ItemContext)selectedItems.get(0)).getProject();
      String remoteUri = repo.getUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }

      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), projectId, remoteUri, null,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {

               @Override
               protected void onSuccess(String result)
               {
                  IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
                  Folder folder = (Folder)selectedItems.get(0);
                  IDE.fireEvent(new RefreshBrowserEvent(getFoldersToRefresh(folder), folder));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  handleError(exception);
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         handleError(e);
      }
   }

   private void handleError(Throwable t)
   {
      String errorMessage =
         (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage() : GitExtension.MESSAGES.cloneFailed();
      IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
   }

   /**
    * Work up to the root folder to create a list of folder to refresh.
    * Need to refresh all folders, that were created during "Select Location"
    * step, but not displayed in main navigation tree.
    * 
    * @param folder - the parent folder of your project
    * @return
    */
   private ArrayList<Folder> getFoldersToRefresh(Folder folder)
   {
      ArrayList<Folder> folders = new ArrayList<Folder>();
      folders.add(0, folder);
      Folder parent = ((ItemContext)folder).getParent();
      while (parent != null)
      {
         folders.add(0, parent);
         parent = ((ItemContext)parent).getParent();
      }
      return folders;
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      vfsBaseUrl = event.getConfiguration().getVfsBaseUrl();
   }

}
