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
package org.exoplatform.ide.client.workspace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.discovery.DiscoveryCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.model.discovery.Scheme;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.preferences.event.SelectWorkspaceHandler;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class SelectWorkspacePresenter implements EditorFileOpenedHandler,
   EditorFileClosedHandler, ApplicationSettingsReceivedHandler, SelectWorkspaceHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

      /*
       * Id of Select Workspace View
       */
      String ID = "ideSelectWorkspaceView";

      /*
       * Returns Workspace list grid
       */
      ListGridItem<EntryPoint> getWorkspaceListGrid();

      /*
       * Returns Ok button
       */
      HasClickHandlers getOkButton();

      /*
       * Returns Cancel button
       */
      HasClickHandlers getCancelButton();

      /*
       * Enables Ok button
       */
      void enableOkButton();

      /*
       * Disables Ok button
       */
      void disableOkButton();

      /**
       * 
       * Selects specified item in 
       * @param currentEntryPoint
       */
      void setSelectedItem(EntryPoint item);

   }

   /**
    * Instanse of Display
    */
   private Display display;

   /**
    * Event Bus
    */
   private HandlerManager eventBus;

   /**
    * Current Workspace, used by IDE
    */
   private String workingWorkspace;

   /**
    * Selected Workspace in Workspace List Grid
    */
   private EntryPoint selectedWorkspace;

   /**
    * Application Settings for retrieving current Workspace and storing selected Workspace
    */
   private ApplicationSettings applicationSettings;

   /**
    * Map of opened files, is needs for verifying for opened files in current working workspace and asking user for save them.
    */
   private Map<String, File> openedFiles = new HashMap<String, File>();

   /*
    * Remove this map and use SaveFileEvent instead calling of VirtualFileSystem.getInstance().saveContent(...) method.
    */
   @Deprecated
   private Map<String, String> lockTokens = new HashMap<String, String>();

   /**
    * List of workspaces for displaying in Workspace List
    */
   private List<EntryPoint> workspaceList = new ArrayList<EntryPoint>();

   public SelectWorkspacePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(SelectWorkspaceEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Handler of ApplicationSettingsReceived Event
    * 
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = applicationSettings.getValueAsMap("lock-tokens");

      workingWorkspace = applicationSettings.getValueAsString("entry-point");
   }

   /**
    * Handler of EditorFileOpened Event.
    * Is need to close opened files while switching workspace.
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * Handler of EditorFileClosed Event.
    * Is need to close opened files while switching workspace.
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    *Handler of selection of the workspace from the list of workspaces.
    * 
    * @see org.exoplatform.ide.client.preferences.event.SelectWorkspaceHandler#onSelectWorkspace(org.exoplatform.ide.client.preferences.event.SelectWorkspaceEvent)
    */
   public void onSelectWorkspace(SelectWorkspaceEvent event)
   {
      DiscoveryService.getInstance().getEntryPoints(new DiscoveryCallback()
      {
         @Override
         protected void onSuccess(List<EntryPoint> result)
         {
            workspaceList = result;
            if (display == null)
            {
               Display d = GWT.create(Display.class);
               IDE.getInstance().openView((ViewEx)d);
               bindDisplay(d);
            }
         }
      });

   }

   /**
    * Binding Display instance after the Display implementation has been created.
    * 
    * @param d
    */
   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            changeEntryPoint();
         }
      });

      display.getWorkspaceListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            onEntryPointDoubleClicked();
         }
      });

      display.getWorkspaceListGrid().addSelectionHandler(new SelectionHandler<EntryPoint>()
      {
         public void onSelection(SelectionEvent<EntryPoint> event)
         {
            onEntryPointSelected(event.getSelectedItem());
         }
      });

      display.disableOkButton();
      updateWorkspacesListGrid();
   }

   /**
    * Update Workspaces List Grid
    */
   private void updateWorkspacesListGrid()
   {
      EntryPoint selectedWorkspace = null;

      List<EntryPoint> workspaces = new ArrayList<EntryPoint>();
      for (int i = 0; i < workspaceList.size(); i++)
      {
         EntryPoint entryPoint = workspaceList.get(i);
         if (entryPoint.getScheme().equals(Scheme.WEBDAV))
         {
            workspaces.add(entryPoint);
            if (entryPoint.getHref().equals(workingWorkspace))
            {
               selectedWorkspace = entryPoint;
            }
         }
      }

      display.getWorkspaceListGrid().setValue(workspaces);
      if (selectedWorkspace != null)
      {
         display.setSelectedItem(selectedWorkspace);
      }
   }

   /**
    * Handler of single clicking on the Workspace List
    * 
    * @param selectedItem
    */
   protected void onEntryPointSelected(EntryPoint selectedItem)
   {
      selectedWorkspace = selectedItem;

      if (selectedWorkspace == null)
      {
         display.disableOkButton();
         return;
      }

      if (selectedWorkspace.getHref().equals(workingWorkspace))
      {
         display.disableOkButton();
      }
      else
      {
         display.enableOkButton();
      }
   }

   /**
    * Handler of Double Clicking on the Workspace List
    */
   protected void onEntryPointDoubleClicked()
   {
      if (selectedWorkspace == null)
      {
         return;
      }

      if (selectedWorkspace.getHref().equals(workingWorkspace))
      {
         return;
      }

      changeEntryPoint();
   }

   /**
    * Changing entry point.
    * Here must be checking for opened files and asking user for saving them.
    */
   private void changeEntryPoint()
   {
      if (openedFiles.size() != 0)
      {
         Dialogs.getInstance().ask("IDEall", "All opened files will be closed.<br>Do you want to continue?",
            new BooleanValueReceivedHandler()
            {
               public void booleanValueReceived(Boolean value)
               {
                  if (value == null)
                  {
                     return;
                  }
                  if (value)
                  {
                     closeNextFile();
                  }
                  else
                  {
                     IDE.getInstance().closeView(Display.ID);
                  }
               }

            });
         return;
      }
      else
      {
         storeCurrentWorkspaceToConfiguration();
      }
   }

   /**
    * Closing opened files.
    */
   private void closeNextFile()
   {
      if (openedFiles.size() == 0)
      {
         storeCurrentWorkspaceToConfiguration();
         return;
      }

      String href = openedFiles.keySet().iterator().next();
      final File file = openedFiles.get(href);

      if (file.isContentChanged())
      {
         String message = "Do you want to save <b>" + Utils.unescape(file.getName()) + "</b> before closing?<br>&nbsp;";
         Dialogs.getInstance().ask("IDEall", message, new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value == null)
               {
                  return;
               }

               if (value)
               {
                  if (file.isNewFile())
                  {
                     eventBus
                        .fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.YES_CANCEL, null, null));
                  }
                  else
                  {
                     VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()),
                        new FileContentSaveCallback()
                        {
                           @Override
                           protected void onSuccess(FileData result)
                           {
                              eventBus.fireEvent(new EditorCloseFileEvent(result.getFile(), true));
                              closeNextFile();
                           }
                        });
                  }
               }
               else
               {
                  eventBus.fireEvent(new EditorCloseFileEvent(file, true));
                  closeNextFile();
               }
            }

         });
         return;
      }
      else
      {
         eventBus.fireEvent(new EditorCloseFileEvent(file, true));
         closeNextFile();
      }
   }

   /**
    * Saving selected workspace to the configuration.
    */
   private void storeCurrentWorkspaceToConfiguration()
   {
      applicationSettings.setValue("entry-point", selectedWorkspace.getHref(), Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
      /*
       * Handle of ApplicationSettingsSaved Event and switch current workspace.
       */
      if (display != null)
      {
         workingWorkspace = selectedWorkspace.getHref();
         IDE.getInstance().closeView(Display.ID);
         eventBus.fireEvent(new SwitchEntryPointEvent(selectedWorkspace.getHref()));
      }
   }

   /**
    * Handler of ViewClosed Event.
    * Clear the display variable if closed view is implementation of the Display.
    * 
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

}
