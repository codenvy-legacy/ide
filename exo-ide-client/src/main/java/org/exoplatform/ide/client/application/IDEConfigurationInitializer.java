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
package org.exoplatform.ide.client.application;

import com.google.gwt.user.client.Timer;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.user.client.Random;

import com.google.gwt.user.client.Window.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.command.ui.SetToolbarItemsEvent;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.discovery.event.IsDiscoverableResultReceivedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.StartWithInitParamsEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.menu.RefreshMenuEvent;
import org.exoplatform.ide.client.model.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.IDEConfigurationUnmarshaller;
import org.exoplatform.ide.client.model.IDEInitializationConfiguration;
import org.exoplatform.ide.client.model.Settings;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.client.model.SettingsServiceImpl;
import org.exoplatform.ide.client.operation.openbypath.OpenFileByPathEvent;
import org.exoplatform.ide.client.workspace.event.SelectWorkspaceEvent;
import org.exoplatform.ide.client.workspace.event.SwitchVFSEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONObject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 25, 2011 evgen $
 * 
 */
public class IDEConfigurationInitializer implements ApplicationSettingsReceivedHandler, VfsChangedHandler,
   ProjectOpenedHandler
{

   private IDEConfiguration applicationConfiguration;

   private ControlsRegistration controls;

   private ApplicationSettings applicationSettings;

   private String initialOpenedProject;

   private List<String> initialOpenedFiles;

   private String initialActiveFile;

   /**
    * @param controls
    */
   public IDEConfigurationInitializer(ControlsRegistration controls)
   {
      super();
      this.controls = controls;
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
   }

   public void loadConfiguration()
   {
      new IDEConfigurationLoader(IDE.eventBus(), IDELoader.get())
         .loadConfiguration(new AsyncRequestCallback<IDEInitializationConfiguration>(new IDEConfigurationUnmarshaller(
            new IDEInitializationConfiguration(), new JSONObject(IDEConfigurationLoader.getAppConfig())))
         {
            @Override
            protected void onSuccess(IDEInitializationConfiguration result)
            {
               try
               {
                  applicationConfiguration = result.getIdeConfiguration();
                  applicationSettings = result.getSettings();
                  IDE.userId = result.getUserInfo().getName();
                  if (result.getUserInfo().getRoles() != null && result.getUserInfo().getRoles().size() > 0)
                  {
                     controls.initControls(result.getUserInfo().getRoles());

                     String registryURLParameter = applicationConfiguration.getRegistryURL();
                     if (registryURLParameter == null)
                     {
                        throw new Exception(org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES
                           .confMissingVariable("registryURL"));
                     }
                     new SettingsServiceImpl(IDE.eventBus(), registryURLParameter, result.getUserInfo().getName(),
                        IDELoader.get(), applicationConfiguration.getContext());
                     SettingsService.getInstance().restoreFromCookies(applicationSettings);

                     initialOpenedProject = applicationSettings.getValueAsString("opened-project");
                     initialActiveFile = applicationSettings.getValueAsString("active-file");

                     initialOpenedFiles = new ArrayList<String>();
                     List<String> openedFiles = applicationSettings.getValueAsList("opened-files");
                     if (openedFiles != null)
                     {
                        initialOpenedFiles.addAll(openedFiles);
                     }

                     IDE.fireEvent(new ConfigurationReceivedSuccessfullyEvent(applicationConfiguration));

                     String hiddenFilesParameter = applicationConfiguration.getHiddenFiles();
                     if (hiddenFilesParameter == null)
                     {
                        throw new Exception(org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES
                           .confMissingVariable("hiddenFiles"));
                     }
                     DirectoryFilter.get().setPattern(hiddenFilesParameter);

                     IDE.fireEvent(new ApplicationSettingsReceivedEvent(result.getSettings()));
                     IDE.fireEvent(new IsDiscoverableResultReceivedEvent(result.isDiscoverable()));
                     IDE.fireEvent(new UserInfoReceivedEvent(result.getUserInfo()));
                     checkEntryPoint();

                  }
                  else
                  {
                     Dialogs.getInstance().showError(org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.userHasNoRoles());
                  }
               }
               catch (Exception e)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(e));
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
   }

   private void checkEntryPoint()
   {
      /*
       * verify entry point
       */
      if (!applicationSettings.containsKey(Settings.ENTRY_POINT) && applicationConfiguration.getVfsId() != null)
      {
         applicationSettings.setValue(Settings.ENTRY_POINT, applicationConfiguration.getVfsId(), Store.COOKIES);
      }

      if (applicationSettings.getValueAsString(Settings.ENTRY_POINT) != null)
      {
         final String entryPoint = applicationSettings.getValueAsString(Settings.ENTRY_POINT);
         IDE.addHandler(VfsChangedEvent.TYPE, this);

         Scheduler.get().scheduleDeferred(new ScheduledCommand()
         {
            @Override
            public void execute()
            {
               IDE.fireEvent(new SwitchVFSEvent(entryPoint));
            }
         });
      }
      else
      {
         promptToSelectEntryPoint();
      }
   }

   public void onVfsChanged(VfsChangedEvent event)
   {
      IDE.removeHandler(VfsChangedEvent.TYPE, this);
      if (event.getVfsInfo() == null || event.getVfsInfo().getId() == null)
      {
         promptToSelectEntryPoint();
      }
      Map<String, List<String>> parameterMap = Location.getParameterMap();
      if (parameterMap != null && !parameterMap.isEmpty())
      {
         IDE.fireEvent(new StartWithInitParamsEvent(parameterMap));
      }
      else
      {
         new RestoreOpenedFilesPhase(applicationSettings, initialOpenedProject, initialOpenedFiles, initialActiveFile);
      }
   }

   protected void promptToSelectEntryPoint()
   {
      // TODO [IDE-307] handle incorrect appConfig["entryPoint"] property value
      Dialogs.getInstance().showError(org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.confWorkspaceWasNotSetTitle(),
         org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.confWorkspaceWasNotSetText(), new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value)
               {
                  IDE.fireEvent(new SelectWorkspaceEvent());
               }
            }
         });
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {

      /*
       * verify toolbar items
       */

      applicationSettings.setValue(Settings.TOOLBAR_DEFAULT_ITEMS, controls.getToolbarDefaultControls(), Store.NONE);
      if (applicationSettings.getValueAsList(Settings.TOOLBAR_ITEMS) == null)
      {
         List<String> toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(controls.getToolbarDefaultControls());
         applicationSettings.setValue(Settings.TOOLBAR_ITEMS, toolbarItems, Store.SERVER);
      }

      initServices();
   }

   private void initServices()
   {
      IDE.fireEvent(new InitializeServicesEvent(applicationConfiguration, IDELoader.get()));

      /*
       * Updating top menu
       */
      IDE.fireEvent(new RefreshMenuEvent());

      List<String> toolbarItems = applicationSettings.getValueAsList(Settings.TOOLBAR_ITEMS);
      if (toolbarItems == null)
      {
         toolbarItems = new ArrayList<String>();
         toolbarItems.addAll(controls.getToolbarDefaultControls());
      }

      IDE.fireEvent(new SetToolbarItemsEvent("exoIDEToolbar", toolbarItems, controls.getRegisteredControls()));
      IDE.fireEvent(new SetToolbarItemsEvent("exoIDEStatusbar", controls.getStatusBarControls(), controls
         .getRegisteredControls()));
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      final Map<String, List<String>> initParam = Location.getParameterMap();
      final ProjectModel project = event.getProject();
      if (initParam != null && !initParam.isEmpty())
      {
         IDE.removeHandler(ProjectOpenedEvent.TYPE, this);
         Timer timer = new Timer()
         {
            public void run()
            {
               if (!initParam.containsKey("v"))
                  return;
               if (initParam.get("v").size() != 1 || !initParam.get("v").get(0).equals("codenow1.0"))
                  return;

               int curx = 0, cury = 0;
               if (initParam.containsKey("curx"))
               {
                  List<String> list = initParam.get("curx");
                  if (!list.isEmpty())
                  {
                     try
                     {
                        curx = Integer.parseInt(list.get(0));
                     }
                     catch (NumberFormatException ignore)
                     {
                        //Nothing todo
                     }
                  }
               }

               if (initParam.containsKey("cury"))
               {
                  List<String> list = initParam.get("cury");
                  if (!list.isEmpty())
                  {
                     try
                     {
                        cury = Integer.parseInt(list.get(0));
                     }
                     catch (NumberFormatException ignor)
                     {
                        //Nothing todo
                     }
                  }
               }

               final CursorPosition cursorPosition = new CursorPosition(cury, curx);

               List<String> openFilePaths = initParam.get("openFilePath");
               if (openFilePaths != null && !openFilePaths.isEmpty())
               {
                  String openFilePath = openFilePaths.get(0);
                  String filePath = project.getPath() + openFilePath;
                  try
                  {
                     VirtualFileSystem.getInstance().getItemByPath(filePath,
                        new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FileModel())))
                        {

                           @Override
                           protected void onSuccess(ItemWrapper result)
                           {
                              result.getItem();
                              FileModel fileModel = new FileModel((File)result.getItem());
                              fileModel.setProject(project);
                              IDE.fireEvent(new GoToItemEvent(fileModel, cursorPosition, true));
                           }

                           @Override
                           protected void onFailure(Throwable exception)
                           {
                              Dialogs.getInstance().showError(exception.getMessage());
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(e));
                  }

               }
            }
            // Execute the timer to expire 2 seconds in the future
         };
         timer.schedule(2000);
      }
   }

   /**
    * Open file and/or go to parent folder.
    * 
    * @param item file which must be opened
    */
   private void doOpenFile(Item item)
   {
      //      if (item.getItemType() == ItemType.FILE)
      //      {
      //         // if tab with file content is active
      //         if (activeFile != null && activeFile.getId().equals(item.getId()))
      //         {
      //            IDE.fireEvent(new GoToFolderEvent());
      //            return;
      //         }
      //
      //         isNeedGoToFolderOnActiveFileChanged = true;
      //
      //         IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.fireEvent(new OpenFileEvent((FileModel)item, new CursorPosition(5, 10)));
      //      }
   }

   /**
    * Retrieves relative path to the file from absolute path.
    * 
    * @param absoluteFilePath link to the file
    * @return relative path to the file
    */
   private String retrieveRelativeFilePath(String absoluteFilePath)
   {
      String vfsURL = VirtualFileSystem.getInstance().getURL();

      if (!absoluteFilePath.startsWith(vfsURL))
      {
         return absoluteFilePath;
      }

      int index = absoluteFilePath.indexOf('/', vfsURL.length() + 1);

      return absoluteFilePath.substring(index + 1);
   }

}
