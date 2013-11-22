/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.application;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.factory.client.receive.StartWithInitParamsEvent;
import com.codenvy.ide.factory.shared.AdvancedFactorySpec;
import com.codenvy.ide.factory.shared.CopySpec10;
import com.codenvy.ide.factory.shared.FactorySpec10;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.command.ui.AddToolbarItemsEvent;
import org.exoplatform.gwtframework.ui.client.command.ui.SetToolbarItemsEvent;
import org.exoplatform.gwtframework.ui.client.command.ui.UniButton;
import org.exoplatform.gwtframework.ui.client.command.ui.UniButton.Size;
import org.exoplatform.gwtframework.ui.client.command.ui.UniButton.Type;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.configuration.IDEInitialConfiguration;
import org.exoplatform.ide.client.framework.configuration.InitialConfigurationReceivedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.menu.RefreshMenuEvent;
import org.exoplatform.ide.client.model.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.IDEConfigurationUnmarshaller;
import org.exoplatform.ide.client.model.Settings;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.client.model.SettingsServiceImpl;
import org.exoplatform.ide.client.workspace.event.SwitchVFSEvent;
import org.exoplatform.ide.extension.samples.client.startpage.ReadOnlyUserView;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 25, 2011 evgen $
 */
public class IDEConfigurationInitializer implements ApplicationSettingsReceivedHandler, VfsChangedHandler

{

    private IDEConfiguration     applicationConfiguration;

    private ControlsRegistration controls;

    private ApplicationSettings  applicationSettings;

    private String               initialOpenedProject;

    private List<String>         initialOpenedFiles;

    private String               initialActiveFile;

    /** @param controls */
    public IDEConfigurationInitializer(ControlsRegistration controls) {
        super();
        this.controls = controls;
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    public void loadConfiguration() {
        new IDEConfigurationLoader(IDE.eventBus(), IDELoader.get()).loadConfiguration(new AsyncRequestCallback<IDEInitialConfiguration>(
                new IDEConfigurationUnmarshaller(new IDEInitialConfiguration(), new JSONObject(IDEConfigurationLoader.getAppConfig()))) {
                           @Override
                           protected void onSuccess(IDEInitialConfiguration result) {
                               try {
                                   applicationConfiguration = result.getIdeConfiguration();
                                   applicationSettings = result.getSettings();
                                   IDE.user = result.getUserInfo();
                                   IDE.currentWorkspace = result.getCurrentWorkspace();

                                   // TODO: small hack need because currently user on client
                                   // must have it least one role
                                   if (result.getUserInfo().getRoles() == null || result.getUserInfo().getRoles().size() == 0) {
                                       result.getUserInfo().setRoles(Arrays.asList("not-in-role"));
                                   }

                                   controls.initControls(result.getUserInfo().getRoles(), result.getCurrentWorkspace());

                                   new SettingsServiceImpl(IDE.eventBus(), result.getUserInfo().getUserId(), IDELoader.get());
                                   SettingsService.getInstance().restoreFromCookies(applicationSettings);

                                   initialOpenedProject = applicationSettings.getValueAsString("opened-project");
                                   initialActiveFile = applicationSettings.getValueAsString("active-file");

                                   initialOpenedFiles = new ArrayList<String>();
                                   List<String> openedFiles = applicationSettings.getValueAsList("opened-files");
                                   if (openedFiles != null) {
                                       initialOpenedFiles.addAll(openedFiles);
                                   }

                                   String hiddenFilesParameter = applicationConfiguration.getHiddenFiles();
                                   if (hiddenFilesParameter == null) {
                                       throw new Exception(org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.confMissingVariable("hiddenFiles"));
                                   }
                                   
                                   DirectoryFilter.get().setPattern(hiddenFilesParameter);

                                   IDE.fireEvent(new InitialConfigurationReceivedEvent(result));

                                   IDE.fireEvent(new ConfigurationReceivedSuccessfullyEvent(applicationConfiguration));
                                   IDE.fireEvent(new ApplicationSettingsReceivedEvent(result.getSettings()));
                                   IDE.fireEvent(new UserInfoReceivedEvent(result.getUserInfo()));
                                   
                                   Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                                       @Override
                                       public void execute() {
                                           checkEntryPoint();
                                       }
                                   });
                               } catch (Exception e) {
                                   IDE.fireEvent(new ExceptionThrownEvent(e));
                               }
                           }

                           @Override
                           protected void onFailure(Throwable exception) {
                               IDE.fireEvent(new ExceptionThrownEvent(exception));
                           }
                       });
    }

    private void checkEntryPoint() {
        /*
         * verify entry point
         */
        if (!applicationSettings.containsKey(Settings.ENTRY_POINT) && applicationConfiguration.getVfsId() != null) {
            applicationSettings.setValue(Settings.ENTRY_POINT, applicationConfiguration.getVfsId(), Store.COOKIES);
        }

        if (applicationSettings.getValueAsString(Settings.ENTRY_POINT) != null) {
            final String entryPoint = applicationSettings.getValueAsString(Settings.ENTRY_POINT);
            IDE.addHandler(VfsChangedEvent.TYPE, this);

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    IDE.fireEvent(new SwitchVFSEvent(entryPoint));
                }
            });
        }
    }

    public void onVfsChanged(VfsChangedEvent event) {
        IDE.removeHandler(VfsChangedEvent.TYPE, this);
        String projectToOpen = Utils.getProjectToOpen();
        if (projectToOpen != null && !projectToOpen.isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemByPath(projectToOpen,
                                                new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {
                                                    @Override
                                                    protected void onSuccess(ItemWrapper result) {
                                                        if (result.getItem() != null && result.getItem() instanceof ProjectModel) {
                                                            ProjectModel projectModel = (ProjectModel)result.getItem();
                                                            initialOpenedProject = projectModel.getId();
                                                            String file = Utils.getFilePathToOpen();
                                                            IDE.fireEvent(new OpenProjectEvent(projectModel));
                                                            if (file != null && !file.isEmpty())
                                                                openFile(file, projectModel);
                                                            else {
                                                                initialActiveFile = null;
                                                                initialOpenedFiles.clear();
                                                                new RestoreOpenedFilesPhase(applicationSettings, initialOpenedProject,
                                                                                            initialOpenedFiles, initialActiveFile);
                                                            }


                                                        }
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        Log.error(AsyncRequestCallback.class, exception);
                                                        initialOpenedProject = null;
                                                        initialOpenedFiles.clear();
                                                        initialActiveFile = null;
                                                        Dialogs.getInstance().showError("Not found resource",
                                                                                        "The requested project URL was not found in this " +
                                                                                            "workspace.");
                                                        new RestoreOpenedFilesPhase(applicationSettings, initialOpenedProject,
                                                                                    initialOpenedFiles, initialActiveFile);
                                                    }
                                                });
            } catch (RequestException e) {
                Log.debug(getClass(), e);
            }

        } else {
            Map<String, List<String>> parameterMap = buildListParamMap(Utils.getStartUpParams());
            if (parameterMap != null && (parameterMap.get(FactorySpec10.FACTORY_VERSION) != null || parameterMap.get(AdvancedFactorySpec.ID) != null)) {
                IDE.fireEvent(new StartWithInitParamsEvent(parameterMap));
            } else if (parameterMap != null && parameterMap.get(CopySpec10.DOWNLOAD_URL) != null
                       && parameterMap.get(CopySpec10.PROJECT_ID) != null) {
                IDE.fireEvent(new StartWithInitParamsEvent(parameterMap));
            } else {
                new RestoreOpenedFilesPhase(applicationSettings, initialOpenedProject, initialOpenedFiles, initialActiveFile);
            }
        }
    }


    private Map<String, List<String>> buildListParamMap(String queryString) {
        Map<String, List<String>> out = new HashMap<String, List<String>>();

        if (queryString != null && queryString.length() > 1) {
            String qs = queryString.substring(1);

            for (String kvPair : qs.split("&")) {
                String[] kv = kvPair.split("=", 2);
                if (kv[0].length() == 0) {
                    continue;
                }

                List<String> values = out.get(kv[0]);
                if (values == null) {
                    values = new ArrayList<String>();
                    out.put(kv[0], values);
                }
                values.add(kv.length > 1 ? URL.decodeQueryString(kv[1]) : "");
            }
        }

        for (Map.Entry<String, List<String>> entry : out.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }

        out = Collections.unmodifiableMap(out);

        return out;
    }

    /** @param file */
    private void openFile(String file, final ProjectModel projectModel) {
        try {
            VirtualFileSystem.getInstance()
                             .getItemByPath(file,
                                            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {
                                                @Override
                                                protected void onSuccess(ItemWrapper result) {
                                                    if (result.getItem() != null && result.getItem() instanceof FileModel) {
                                                        FileModel fileModel = (FileModel)result.getItem();
                                                        fileModel.setProject(projectModel);
                                                        initialActiveFile = fileModel.getId();
                                                        initialOpenedFiles.clear();
                                                        initialOpenedFiles.add(fileModel.getId());
                                                        new RestoreOpenedFilesPhase(applicationSettings, initialOpenedProject,
                                                                                    initialOpenedFiles, initialActiveFile);
                                                    }
                                                }

                                                @Override
                                                protected void onFailure(Throwable exception) {
                                                    Log.error(AsyncRequestCallback.class, exception);
                                                    Dialogs.getInstance().showError("Not found resource",
                                                                                    "The requested file URL was not found on this project" +
                                                                                        ".");
                                                    initialActiveFile = null;
                                                    initialOpenedFiles.clear();
                                                    new RestoreOpenedFilesPhase(applicationSettings, initialOpenedProject,
                                                                                initialOpenedFiles, initialActiveFile);
                                                }
                                            });
        } catch (RequestException e) {
            Log.debug(getClass(), e);
        }
    }


    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {

        /*
         * verify toolbar items
         */

        applicationSettings.setValue(Settings.TOOLBAR_DEFAULT_ITEMS, controls.getToolbarDefaultControls(), Store.NONE);
        if (applicationSettings.getValueAsList(Settings.TOOLBAR_ITEMS) == null) {
            List<String> toolbarItems = new ArrayList<String>();
            toolbarItems.addAll(controls.getToolbarDefaultControls());
            applicationSettings.setValue(Settings.TOOLBAR_ITEMS, toolbarItems, Store.SERVER);
        }
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                initServices();
            }
        });
    }

    private void initServices() {

        IDE.fireEvent(new InitializeServicesEvent(applicationConfiguration, IDELoader.get()));

        /*
         * Updating top menu
         */
        IDE.fireEvent(new RefreshMenuEvent());

        List<String> toolbarItems = applicationSettings.getValueAsList(Settings.TOOLBAR_ITEMS);
        if (toolbarItems == null) {
            toolbarItems = new ArrayList<String>();
            toolbarItems.addAll(controls.getToolbarDefaultControls());
        }

        IDE.fireEvent(new SetToolbarItemsEvent("exoIDEToolbar", toolbarItems, controls.getRegisteredControls()));
        IDE.fireEvent(new SetToolbarItemsEvent("exoIDEStatusbar", controls.getStatusBarControls(), controls.getRegisteredControls()));

        if (IDE.isRoUser()) {
            UniButton readOnlyButton = new UniButton("Read-only", new Image(IDEImageBundle.INSTANCE.readOnlyIcon()), Type.PRIMARY, Size.SMALL);
            IDE.fireEvent(new AddToolbarItemsEvent(readOnlyButton, true));
            readOnlyButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    IDE.getInstance().openView(new ReadOnlyUserView(IDE.user.getWorkspaces()));
                }
            });
            
        }
    }

}
