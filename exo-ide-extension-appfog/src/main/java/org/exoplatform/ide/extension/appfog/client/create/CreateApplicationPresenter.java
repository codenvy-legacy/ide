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
package org.exoplatform.ide.extension.appfog.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.extension.appfog.client.*;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.FrameworksUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.marshaller.InfrasUnmarshaller;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.InfraDetail;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter for creating application on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateApplicationPresenter extends GitPresenter implements CreateApplicationHandler, ViewClosedHandler,
                                                                        ProjectBuiltHandler {
    interface Display extends IsView {
        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getTypeField();

        HasValue<Boolean> getAutodetectTypeCheckItem();

        HasValue<String> getNameField();

        HasValue<String> getUrlField();

        HasValue<String> getInfraField();

        HasValue<Boolean> getUrlCheckItem();

        HasValue<String> getInstancesField();

        HasValue<String> getMemoryField();

        HasValue<String> getServerField();

        HasValue<Boolean> getIsStartAfterCreationCheckItem();

        void setIsStartAfterCreationCheckItem(boolean start);

        void enableCreateButton(boolean enable);

        void focusInNameField();

        void setTypeValues(String[] types);

        void enableTypeField(boolean enable);

        void enableUrlField(boolean enable);

        void enableMemoryField(boolean enable);

        void setSelectedIndexForTypeSelectItem(int index);

        void focusInUrlField();

        void enableAutodetectTypeCheckItem(boolean enable);

        void setServerValue(String server);

        void setInfraValues(String[] infras);

    }

    private class AppData {
        String server;

        String name;

        String type;

        String url;

        String infra;

        int instances;

        int memory;

        boolean nostart;

        public AppData(String server, String name, String type, String url, int instances, int memory, boolean nostart,
                       String infra) {
            this.server = server;
            this.name = name;
            this.type = type;
            this.url = url;
            this.instances = instances;
            this.memory = memory;
            this.nostart = nostart;
            this.infra = infra;
        }
    }

    private static final AppfogLocalizationConstant lb = AppfogExtension.LOCALIZATION_CONSTANT;

    private Display display;

    private List<Framework> frameworks;

    private InfraDetail currentInfra;

    private List<InfraDetail> infras;

    /** Public url to war file of application. */
    private String warUrl;

    /** Store application data in format, that convenient to send to server. */
    private AppData appData;

    /**
     *
     */
    private boolean isMavenProject;

    public CreateApplicationPresenter() {
        IDE.addHandler(CreateApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                closeView();
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                appData = getAppDataFromForm();

                validateData(appData);
            }
        });

        display.getAutodetectTypeCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                display.enableTypeField(!event.getValue());
                display.enableMemoryField(!event.getValue());

                if (event.getValue()) {
                    display.setTypeValues(new String[]{""});
                    display.getMemoryField().setValue("");
                } else {
                    final String[] frameworkArray = getApplicationTypes(frameworks);
                    display.setTypeValues(frameworkArray);
                    display.setSelectedIndexForTypeSelectItem(0);
                    getFrameworks(display.getServerField().getValue());
                }
            }
        });

        display.getUrlCheckItem().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                display.enableUrlField(event.getValue());

                if (event.getValue()) {
                    display.focusInUrlField();
                } else {
                    updateUrlField();
                }
            }
        });

        display.getTypeField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                Framework framework = findFrameworkByName(event.getValue());
                if (framework != null) {
                    display.getMemoryField().setValue(String.valueOf(framework.getMemory()));
                }
            }
        });

        display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // if url set automatically, than try to create url using server and name
                if (!display.getUrlCheckItem().getValue()) {
                    updateUrlField();
                }
            }
        });

        display.getServerField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                // if url set automatically, than try to create url using server and name
                if (!display.getUrlCheckItem().getValue()) {
                    display.enableAutodetectTypeCheckItem(true);
                }
            }
        });

        display.getInfraField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                currentInfra = findInfraByName(display.getInfraField().getValue());
                updateUrlField();
            }
        });

        // set the state of fields
        display.enableTypeField(false);
        display.enableUrlField(false);
        display.enableMemoryField(false);
        display.focusInNameField();

        // set default values to fields
        display.setTypeValues(new String[]{""});
        display.getInstancesField().setValue("1");
        display.getAutodetectTypeCheckItem().setValue(true);
    }

//   /**
//    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
// .framework.navigation.event.ItemsSelectedEvent)
//    */
//   @Override
//   public void onItemsSelected(ItemsSelectedEvent event)
//   {
//      selectedItems = event.getSelectedItems();
//   }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide
     * .extension.cloudfoundry.client.create.CreateApplicationEvent) */
    @Override
    public void onCreateApplication(CreateApplicationEvent event) {
        final ProjectModel project = getSelectedProject();

        if (project == null) {
            String msg = AppfogExtension.LOCALIZATION_CONSTANT.selectFolderToCreate();
            IDE.fireEvent(new ExceptionThrownEvent(msg));
            return;
        }

        if (project.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(project.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(project))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      project.setLinks(result.getItem().getLinks());
                                                      checkIsProject(project);
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                  }
                                              });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            checkIsProject(project);
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven
     * .client.event.ProjectBuiltEvent) */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication(appData);
        }
    }

    // ----Implementation------------------------

    private String getUrlByServerAndName(String serverUrl, String name) {
        return "http://" + name.toLowerCase() + '.' + serverUrl;
    }

    /** Update the URL field, using values from server and name field. */
    private void updateUrlField() {
        final String url = getUrlByServerAndName(currentInfra.getBase(), display.getNameField().getValue());
        display.getUrlField().setValue(url);
    }

    private void validateData(final AppData app) {
        LoggedInHandler validateHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                validateData(app);
            }
        };

//      ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        ProjectModel project = getSelectedProject();

        try {
            AppfogClientService.getInstance().validateAction("create", app.server, app.name, app.type, app.url,
                                                             vfs.getId(), project.getId(), app.instances, app.memory, app.nostart,
                                                             new AppfogAsyncRequestCallback<String>(null, validateHandler, null,
                                                                                                    app.server) {
                                                                 @Override
                                                                 protected void onSuccess(String result) {
                                                                     if (isMavenProject) {
                                                                         buildApplication();
                                                                     } else {
                                                                         createApplication(appData);
                                                                     }
                                                                     closeView();
                                                                 }
                                                             });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getFrameworks(final String server) {
        LoggedInHandler getFrameworksLoggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getFrameworks(server);
            }
        };

        try {
            AppfogClientService.getInstance().getFrameworks(
                    new AppfogAsyncRequestCallback<List<Framework>>(new FrameworksUnmarshaller(new ArrayList<Framework>()),
                                                                    getFrameworksLoggedInHandler, null) {
                        @Override
                        protected void onSuccess(List<Framework> result) {
                            if (!result.isEmpty()) {
                                frameworks = result;
                                String[] fw = new String[result.size()];
                                int i = 0;
                                for (Framework framework : result) {
                                    fw[i++] = framework.getDisplayName() != null ? framework.getDisplayName() : framework.getName();
                                }
                                display.setTypeValues(fw);
                                Framework framework = findFrameworkByName(fw[0]);
                                display.getMemoryField().setValue(String.valueOf(framework.getMemory()));
                            }

                        }
                    }, server);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void buildApplication() {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildProjectEvent());
    }

    /**
     * Create application on AppFog by sending request over WebSocket or HTTP.
     *
     * @param appData
     *         data to create new application
     */
    private void createApplication(final AppData appData) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication(appData);
            }
        };

//      final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        final ProjectModel project = getSelectedProject();

        try {
            AutoBean<AppfogApplication> appfogApplication = AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();
            AutoBeanUnmarshallerWS<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshallerWS<AppfogApplication>(appfogApplication);
            AppfogClientService.getInstance().createWS(appData.server, appData.name, appData.type, appData.url,
                                                       appData.instances, appData.memory, appData.nostart, vfs.getId(), project.getId(),
                                                       warUrl, appData.infra,
                                                       new AppfogRESTfulRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler,
                                                                                                           null, appData.server) {
                                                           @Override
                                                           protected void onSuccess(AppfogApplication result) {
                                                               onAppCreatedSuccess(result);
                                                               IDE.fireEvent(new RefreshBrowserEvent(project));
                                                           }

                                                           @Override
                                                           protected void onFailure(Throwable exception) {
                                                               IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(),
                                                                                             OutputMessage.Type.INFO));
                                                               super.onFailure(exception);
                                                           }
                                                       });
        } catch (WebSocketException e) {
            createApplicationREST(appData, project, loggedInHandler);
        }
    }

    /**
     * Create application on AppFog by sending request over HTTP.
     *
     * @param appData
     *         data to create new application
     * @param project
     *         {@link ProjectModel}
     * @param loggedInHandler
     *         handler that should be called after success login
     */
    private void createApplicationREST(final AppData appData, final ProjectModel project, LoggedInHandler loggedInHandler) {
        try {
            AutoBean<AppfogApplication> appfogApplication = AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);
            AppfogClientService.getInstance().create(appData.server, appData.name, appData.type, appData.url,
                                                     appData.instances, appData.memory, appData.nostart, vfs.getId(), project.getId(),
                                                     warUrl, appData.infra,
                                                     new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null,
                                                                                                       appData.server) {
                                                         @Override
                                                         protected void onSuccess(AppfogApplication result) {
                                                             onAppCreatedSuccess(result);
                                                             IDE.fireEvent(new RefreshBrowserEvent(project));
                                                         }

                                                         @Override
                                                         protected void onFailure(Throwable exception) {
                                                             IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(),
                                                                                           OutputMessage.Type.INFO));
                                                             super.onFailure(exception);
                                                         }
                                                     });
        } catch (RequestException e) {
            IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
        }
    }

    /**
     * Performs action when application successfully created.
     *
     * @param app
     *         @link AppfogApplication} which is created
     */
    private void onAppCreatedSuccess(AppfogApplication app) {
        warUrl = null;

        if ("STARTED".equals(app.getState()) && app.getInstances() == app.getRunningInstances()) {
            String msg = lb.applicationCreatedSuccessfully(app.getName());
            if (app.getUris().isEmpty()) {
                msg += "<br>" + lb.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + lb.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
            IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        } else if ("STARTED".equals(app.getState()) && app.getInstances() != app.getRunningInstances()) {
            String msg = lb.applicationWasNotStarted(app.getName());
            IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.ERROR));
        } else {
            String msg = lb.applicationCreatedSuccessfully(app.getName());
            IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        }
    }

    /**
     * Get the array of application types from list of frameworks.
     *
     * @param frameworks
     *         - list of available frameworks
     * @return an array of types
     */
    private String[] getApplicationTypes(List<Framework> frameworks) {
        List<String> frameworkNames = new ArrayList<String>();
        for (Framework framework : frameworks) {
            frameworkNames.add(framework.getDisplayName() != null ? framework.getDisplayName() : framework.getName());
        }

        return frameworkNames.toArray(new String[frameworkNames.size()]);
    }

    /**
     * Find framework from list by name.
     *
     * @param frameworkName
     * @return
     */
    private Framework findFrameworkByName(String frameworkName) {
        for (Framework framework : frameworks) {
            String name = framework.getDisplayName() != null ? framework.getDisplayName() : framework.getName();
            if (frameworkName.equals(name)) {
                return framework;
            }
        }
        return null;
    }

    private void openView(List<Framework> frameworks) {
        if (display == null) {
            display = GWT.create(Display.class);
            this.frameworks = frameworks;
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            display.focusInNameField();
            display.setServerValue(AppfogExtension.DEFAULT_SERVER);
            getInfras(AppfogExtension.DEFAULT_SERVER);
            // Application will be started after creation (IDE-1618)
            display.setIsStartAfterCreationCheckItem(true);
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("View Create Appfog Application must be null"));
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
    }

    private String getAppUrlsAsString(AppfogApplication application) {
        String appUris = "";
        for (String uri : application.getUris()) {
            if (!uri.startsWith("http")) {
                uri = "http://" + uri;
            }
            appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        }
        if (!appUris.isEmpty()) {
            // crop unnecessary symbols
            appUris = appUris.substring(2);
        }
        return appUris;
    }

    /**
     * Process values from application create form, and store data in bean in format, that is convenient to send to server
     *
     * @return {@link AppData}
     */
    private AppData getAppDataFromForm() {
        String server = display.getServerField().getValue();
        if (server == null || server.isEmpty()) {
            server = AppfogExtension.DEFAULT_SERVER;
        } else if (server.endsWith("/")) {
            server = server.substring(0, server.length() - 1);
        }
        String name = display.getNameField().getValue();
        String type;
        int memory = 0;
        if (display.getAutodetectTypeCheckItem().getValue()) {
            type = null;
            memory = 0;
        } else {
            Framework framework = findFrameworkByName(display.getTypeField().getValue());
            type = framework.getName();
            try {
                memory = Integer.parseInt(display.getMemoryField().getValue());
            } catch (NumberFormatException e) {
                IDE.fireEvent(new ExceptionThrownEvent(AppfogExtension.LOCALIZATION_CONSTANT.errorMemoryFormat()));
            }
        }

        String url;

        InfraDetail infra = findInfraByName(display.getInfraField().getValue());

        if (display.getUrlCheckItem().getValue()) {
            url = display.getUrlField().getValue();
            if (url == null || url.isEmpty()) {
                url = null;
            }
        } else {
            url = name + '.' + infra.getBase();
        }

        int instances = 0;
        try {
            instances = Integer.parseInt(display.getInstancesField().getValue());
        } catch (NumberFormatException e) {
            IDE.fireEvent(new ExceptionThrownEvent(AppfogExtension.LOCALIZATION_CONSTANT.errorInstancesFormat()));
        }
        boolean nostart = !display.getIsStartAfterCreationCheckItem().getValue();

        return new AppData(server, name, type, url, instances, memory, nostart, infra.getInfra());
    }

    private InfraDetail findInfraByName(String infraName) {
        for (InfraDetail infra : infras) {
            if (infraName.equals(infra.getName())) {
                return infra;
            }
        }
        return null;
    }

    /** Check is selected item project and can be built. */
    private void checkIsProject(final ProjectModel project) {
        try {
            VirtualFileSystem.getInstance().getChildren(project,
                                                        new AsyncRequestCallback<List<Item>>(
                                                                new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                                            @Override
                                                            protected void onSuccess(List<Item> result) {
                                                                isMavenProject = false;
                                                                project.getChildren().setItems(result);
                                                                for (Item i : result) {
                                                                    if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName())) {
                                                                        isMavenProject = true;
                                                                    }
                                                                }
                                                                openView(Collections.<Framework>emptyList());
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                       "Service is not deployed.<br>Parent folder not found."));
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getInfras(final String server) {
        LoggedInHandler getInfrasHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getInfras(server);
            }
        };

        try {
            AppfogClientService.getInstance().infras(
                    server,
                    null,
                    null,
                    new AppfogAsyncRequestCallback<List<InfraDetail>>(new InfrasUnmarshaller(new ArrayList<InfraDetail>()),
                                                                      getInfrasHandler, null, server) {
                        @Override
                        protected void onSuccess(List<InfraDetail> result) {
                            if (result.isEmpty()) {
                                IDE.fireEvent(new ExceptionThrownEvent(AppfogExtension.LOCALIZATION_CONSTANT.errorGettingInfras()));
                            } else {
                                infras = result;

                                List<String> infraNames = new ArrayList<String>(result.size());
                                for (InfraDetail infra : result) {
                                    infraNames.add(infra.getName());
                                }

                                display.getInfraField().setValue(infraNames.get(0));
                                display.setInfraValues(infraNames.toArray(new String[infraNames.size()]));
                                display.getServerField().setValue(display.getServerField().getValue());

                                currentInfra = infras.get(0);
                            }
                            updateUrlField();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
