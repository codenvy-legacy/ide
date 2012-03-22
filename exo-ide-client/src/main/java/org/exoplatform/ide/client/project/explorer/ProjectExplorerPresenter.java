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

package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectHandler;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerPresenter implements ShowProjectExplorerHandler, ApplicationSettingsReceivedHandler,
   VfsChangedHandler, OpenProjectHandler, CloseProjectHandler, ViewClosedHandler, RefreshBrowserHandler,
   SelectItemHandler

//, ,
//ViewVisibilityChangedHandler, ItemUnlockedHandler, ItemLockedHandler, ,
//, AddItemTreeIconHandler, RemoveItemTreeIconHandler, ShowProjectExplorerHandler,
//ItemsSelectedHandler, ViewActivatedHandler, , , ,
//AllFilesClosedHandler, GoToFolderHandler, EditorActiveFileChangedHandler, IDELoadCompleteHandler,
//EditorFileOpenedHandler, EditorFileClosedHandler

{

   public interface Display extends IsView
   {

      static final String TITLE = "Project Explorer";

      ItemTree itemTree();

      void setProject(ProjectModel project);

      HasClickHandlers getLinkWithEditorButton();

      void setLinkWithEditorButtonEnabled(boolean enabled);

      void setLinkWithEditorButtonSelected(boolean selected);

   }

   /**
    * Instance of {@link Display}
    */
   private Display display;

   /**
    * Opened Project
    */
   private ProjectModel project;

   /**
    * IDE Settings
    */
   private ApplicationSettings settings;

   /**
    * Enabled or disabled Linking with Editor.
    */
   private boolean linkingWithEditor = false;

   public ProjectExplorerPresenter()
   {
      IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(OpenProjectEvent.TYPE, this);
      IDE.addHandler(CloseProjectEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(SelectItemEvent.TYPE, this);
   }

   /**
    * Adds actions to display.
    */
   private void bindDisplay()
   {
      display.itemTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         @Override
         public void onSelection(SelectionEvent<Item> event)
         {
            List<Item> selectedItems = new ArrayList<Item>(display.itemTree().getSelectedSet());
            IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
         }
      });

      display.itemTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            List<Item> selectedItems = new ArrayList<Item>(display.itemTree().getSelectedSet());
            if (selectedItems.size() > 0)
            {
               Item item = selectedItems.get(0);
               if (item instanceof File)
               {
                  IDE.fireEvent(new OpenFileEvent((FileModel)item));
               }
            }
         }
      });

   }

   @Override
   public void onShowProjectExplorer(ShowProjectExplorerEvent event)
   {
      if (display == null)
      {
         openProject(project);
      }
      else
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      settings = event.getApplicationSettings();

      if (settings.getValueAsMap("lock-tokens") == null)
      {
         settings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      if (settings.getValueAsBoolean("project-explorer-linked-with-editor") == null)
      {
         settings.setValue("project-explorer-linked-with-editor", Boolean.FALSE, Store.COOKIES);
      }

      linkingWithEditor = settings.getValueAsBoolean("project-explorer-linked-with-editor");

      //      ensureProjectExplorerDisplayCreated();
      //      display.setLockTokens(applicationSettings.getValueAsMap("lock-tokens"));

      display.setLinkWithEditorButtonSelected(linkingWithEditor);
      if (project == null)
      {
         display.setLinkWithEditorButtonEnabled(false);
      }
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      if (display == null)
      {
         return;
      }

      display.setProject(null);
      display.asView().setTitle(Display.TITLE);
   }

   @Override
   public void onOpenProject(OpenProjectEvent event)
   {
      if (event.getProject() == null)
      {
         return;
      }

      if (project != null && project.getId().equals(event.getProject().getId())
         && project.getName().equals(event.getProject().getName()))
      {
         return;
      }

      openProject(event.getProject());
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new ProjectOpenedEvent(project));
         }
      });
   }

   @Override
   public void onCloseProject(CloseProjectEvent event)
   {
      if (display == null || project == null)
      {
         return;
      }

      ProjectModel curProject = project;
      project = null;
      display.setProject(null);
      display.asView().setTitle(Display.TITLE);
      IDE.fireEvent(new ProjectClosedEvent(curProject));
   }

   public void openProject(ProjectModel project)
   {
      this.project = project;

      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }

      display.setProject(project);
      if (project != null)
      {
         display.asView().setTitle(project.getName());
      }
      else
      {
         display.asView().setTitle(Display.TITLE);
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      if (!display.asView().isActive())
      {
         return;
      }

      Set<Item> selectedItems = display.itemTree().getSelectedSet();
      List<Item> items = new ArrayList<Item>(selectedItems);
      if (items.size() == 1)
      {
         Item selectedItem = items.get(0);

         if (selectedItem instanceof Folder)
         {
            display.itemTree().refreshFolder((Folder)selectedItem);
         }
         else
         {
            String path = selectedItem.getPath();
            path = path.substring(0, path.lastIndexOf("/"));
            Folder folder = new Folder();
            folder.setPath(path);
            display.itemTree().refreshFolder(folder);
         }
      }
   }

   @Override
   public void onSelectItem(SelectItemEvent event)
   {
      System.out.println("ProjectExplorerPresenter.onSelectItem()");
   }

}
