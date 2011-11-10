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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.workspace.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.workspace.event.SelectWorkspaceHandler;
import org.exoplatform.ide.client.workspace.event.SwitchVFSEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystemFactory;
import org.exoplatform.ide.vfs.client.marshal.VFSListUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class SelectWorkspacePresenter implements ApplicationSettingsReceivedHandler, SelectWorkspaceHandler, ViewClosedHandler, AllFilesClosedHandler
{

   public interface Display extends IsView
   {

      /*
       * Returns Workspace list grid
       */
      ListGridItem<VirtualFileSystemInfo> getWorkspaceListGrid();

      /*
       * Returns Ok button
       */
      HasClickHandlers getOkButton();

      /*
       * Returns Cancel button
       */
      HasClickHandlers getCancelButton();

      /**
       * Enables or disables Ok button.
       * 
       * @param enabled is Ok button enabled
       */
      void setOkButtonEnabled(boolean enabled);

      /**
       * 
       * Selects specified item in 
       * @param currentEntryPoint
       */
      void setSelectedItem(VirtualFileSystemInfo item);

   }


   /**
    * Instance of Display
    */
   private Display display;

   /**
    * Current Workspace, used by IDE
    */
   private String workingWorkspace;

   /**
    * Selected Workspace in Workspace List Grid
    */
   private VirtualFileSystemInfo selectedWorkspace;

   /**
    * Application Settings for retrieving current Workspace and storing selected Workspace
    */
   private ApplicationSettings applicationSettings;

   /**
    * Map of opened files, is needs for verifying for opened files in current working workspace and asking user for save them.
    */
   //private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   /*
    * Remove this map and use SaveFileEvent instead calling of VirtualFileSystem.getInstance().saveContent(...) method.
    */
   @Deprecated
   private Map<String, String> lockTokens = new HashMap<String, String>();

   /**
    * List of workspaces for displaying in Workspace List
    */
   private List<VirtualFileSystemInfo> workspaceList = new ArrayList<VirtualFileSystemInfo>();

   public SelectWorkspacePresenter()
   {
      IDE.getInstance().addControl(new SelectWorkspaceControl());

      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(SelectWorkspaceEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(AllFilesClosedEvent.TYPE, this);
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
    *Handler of selection of the workspace from the list of workspaces.
    * 
    * @see org.exoplatform.ide.client.workspace.event.SelectWorkspaceHandler#onSelectWorkspace(org.exoplatform.ide.client.workspace.event.SelectWorkspaceEvent)
    */
   public void onSelectWorkspace(SelectWorkspaceEvent event)
   {
      if (display != null)
      {
         return;
      }

      try
      {
         VirtualFileSystemFactory.getInstance().getAvailableFileSystems(
            new AsyncRequestCallback<List<VirtualFileSystemInfo>>(new VFSListUnmarshaller(
               new ArrayList<VirtualFileSystemInfo>()))
            {

               @Override
               protected void onSuccess(List<VirtualFileSystemInfo> result)
               {
                  workspaceList = result;

                  display = GWT.create(Display.class);
                  IDE.getInstance().openView(display.asView());
                  bindDisplay();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Binding Display instance after the Display implementation has been created.
    * 
    * @param d
    */
   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            IDE.getInstance().closeView(display.asView().getId());
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
            onVFSDoubleClicked();
         }
      });

      display.getWorkspaceListGrid().addSelectionHandler(new SelectionHandler<VirtualFileSystemInfo>()
      {
         public void onSelection(SelectionEvent<VirtualFileSystemInfo> event)
         {
            onVFSSelected(event.getSelectedItem());
         }
      });

      display.setOkButtonEnabled(false);
      updateVFSListGrid();
   }

   /**
    * Update Workspaces List Grid
    */
   private void updateVFSListGrid()
   {
      VirtualFileSystemInfo selectedWorkspace = null;

      List<VirtualFileSystemInfo> workspaces = new ArrayList<VirtualFileSystemInfo>();
      for (int i = 0; i < workspaceList.size(); i++)
      {
         VirtualFileSystemInfo entryPoint = workspaceList.get(i);
         workspaces.add(entryPoint);
         if (entryPoint.getId().equals(workingWorkspace))
         {
            selectedWorkspace = entryPoint;
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
   protected void onVFSSelected(VirtualFileSystemInfo selectedItem)
   {
      selectedWorkspace = selectedItem;

      if (selectedWorkspace == null)
      {
         display.setOkButtonEnabled(false);
         return;
      }

      boolean currentVFSSelected = selectedWorkspace.getId().equals(workingWorkspace);
      display.setOkButtonEnabled(!currentVFSSelected);
   }

   /**
    * Handler of Double Clicking on the Workspace List
    */
   protected void onVFSDoubleClicked()
   {
      if (selectedWorkspace == null)
      {
         return;
      }
      if (selectedWorkspace.getId().equals(workingWorkspace))
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
      IDE.fireEvent(new CloseAllFilesEvent());
   }
   
   @Override
   public void onAllFilesClosed(AllFilesClosedEvent event)
   {
      if (display == null) {
         return;
      }
      
      storeCurrentWorkspaceToConfiguration();
   }

   /**
    * Saving selected workspace to the configuration.
    */
   private void storeCurrentWorkspaceToConfiguration()
   {
      applicationSettings.setValue("entry-point", selectedWorkspace.getId(), Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
      //Handle of ApplicationSettingsSaved Event and switch current workspace.
      if (display != null)
      {
         workingWorkspace = selectedWorkspace.getId();
         IDE.getInstance().closeView(display.asView().getId());
         IDE.fireEvent(new SwitchVFSEvent(selectedWorkspace.getId()));
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
