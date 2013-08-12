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
package com.codenvy.ide.ext.openshift.client.wizard;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientService;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.ext.openshift.client.OpenShiftWSRequestCallback;
import com.codenvy.ide.ext.openshift.client.key.UpdateKeyPresenter;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.client.marshaller.ApplicationInfoUnmarshallerWS;
import com.codenvy.ide.ext.openshift.client.marshaller.ListUnmarshaller;
import com.codenvy.ide.ext.openshift.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Wizard page for creating project on OpenShift.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class OpenShiftPagePresenter extends AbstractWizardPagePresenter implements OpenShiftPageView.ActionDelegate {
    private OpenShiftPageView             view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private ResourceProvider              resourceProvider;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private OpenShiftClientService        service;
    private TemplateAgent                 templateAgent;
    private CreateProjectProvider         createProjectProvider;
    private UpdateKeyPresenter            updateKeyPresenter;
    private GitClientService              gitService;
    private ProjectExplorerPartPresenter  projectExplorer;
    private OpenShiftPagePresenter        instance;
    private boolean                       isLogged;
    private Project                       project;
    private String                        projectName;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param resourceProvider
     * @param constant
     * @param loginPresenter
     * @param service
     * @param templateAgent
     * @param resources
     * @param updateKeyPresenter
     * @param gitService
     * @param projectExplorer
     * @param createProjectProvider
     */
    @Inject
    protected OpenShiftPagePresenter(OpenShiftPageView view, EventBus eventBus, ConsolePart console, ResourceProvider resourceProvider,
                                     OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter, OpenShiftClientService service,
                                     TemplateAgent templateAgent, OpenShiftResources resources, UpdateKeyPresenter updateKeyPresenter,
                                     GitClientService gitService, ProjectExplorerPartPresenter projectExplorer,
                                     CreateProjectProvider createProjectProvider) {
        super("Deploy project to OpenShift", resources.openShift48());

        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.templateAgent = templateAgent;
        this.updateKeyPresenter = updateKeyPresenter;
        this.gitService = gitService;
        this.projectExplorer = projectExplorer;
        this.instance = this;
        this.createProjectProvider = createProjectProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void onApplicationNameChanged() {
        projectName = view.getName();
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (!isLogged) {
            return "This project will not be created without deploy on OpenShift.";
        } else if (view.getName().isEmpty()) {
            return "Please, enter a application's name.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        Provider<OpenShiftPagePresenter> wizardInstance = new Provider<OpenShiftPagePresenter>() {
            @Override
            public OpenShiftPagePresenter get() {
                return instance;
            }
        };

        Template template = new Template(null, "OpenShift", createProjectProvider, wizardInstance, JsonCollections.createArray("War"));
        createProjectProvider = template.getCreateProjectProvider();
        createProjectProvider.setProjectName(templateAgent.getSelectedTemplate().getCreateProjectProvider().getProjectName());
        projectName = createProjectProvider.getProjectName();

        view.setName(projectName);

        getApplicationTypes();

        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        createEmptyProject();
    }

    /** Get application types supported by OpenShift. */
    private void getApplicationTypes() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationTypes();
            }
        };

        try {
            ListUnmarshaller unmarshaller = new ListUnmarshaller(new ArrayList<String>());
            service.getApplicationTypes(
                    new OpenShiftAsyncRequestCallback<List<String>>(unmarshaller, loggedInHandler, null, eventBus, console, constant,
                                                                    loginPresenter) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            isLogged = true;
                            JsonArray<String> types = JsonCollections.createArray(result);
                            view.setApplicationTypes(types);
                        }
                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Validate filling application name field.
     *
     * @return true - if user entered application name and hi is loggined in, otherwise false
     */
    private boolean validate() {
        return !isLogged || view.getName() != null && !view.getName().isEmpty();
    }

    /** Create empty project, without any files. */
    private void createEmptyProject() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable caught) {
                console.print(caught.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(caught));
            }

            @Override
            public void onSuccess(Project result) {
                projectExplorer.setContent(null);
                if (isLogged) {
                    project = result;
                    createApplication();
                }
            }
        });
    }

    /** Create application on OpenShift and after that start to update public key. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };

        try {
            DtoClientImpls.AppInfoImpl appInfo = DtoClientImpls.AppInfoImpl.make();
            ApplicationInfoUnmarshallerWS unmarshaller = new ApplicationInfoUnmarshallerWS(appInfo);

            service.createApplicationWS(projectName, resourceProvider.getVfsId(), project.getId(), view.getApplicationType(),
                                        view.getScalingValue(),
                                        new OpenShiftWSRequestCallback<AppInfo>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                                loginPresenter) {


                                            @Override
                                            protected void onSuccess(AppInfo result) {
                                                updatePublicKey(result);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                super.onFailure(exception);
                                                createApplicationRest();

                                            }
                                        });
        } catch (WebSocketException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Create application on OpenShift over Rest and after that start to update public key. */
    private void createApplicationRest() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplicationRest();
            }
        };

        try {
            DtoClientImpls.AppInfoImpl appInfo = DtoClientImpls.AppInfoImpl.make();
            ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(appInfo);

            service.createApplication(projectName, resourceProvider.getVfsId(), project.getId(), view.getApplicationType(),
                                      view.getScalingValue(),
                                      new OpenShiftAsyncRequestCallback<AppInfo>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                                 constant, loginPresenter) {


                                          @Override
                                          protected void onSuccess(AppInfo result) {
                                              updatePublicKey(result);
                                          }

                                          @Override
                                          protected void onFailure(Throwable exception) {
                                              super.onFailure(exception);
                                              //TODO cleanup project if creation of application on OpenShift is failed.
                                          }
                                      });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Update public ssh key. After successfully update of a key starts pulling source code from OpenShift over git.
     *
     * @param application
     *         information about newly created application
     */
    private void updatePublicKey(final AppInfo application) {
        updateKeyPresenter.updatePublicKey(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                String msg = constant.applicationPublicKeyUpdateFailed();
                console.print(msg);
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    pullSources(application);
                } else {
                    String msg = constant.applicationPublicKeyUpdateFailed();
                    console.print(msg);
                }
            }
        });
    }

    /**
     * Starts pulling source files over git.
     *
     * @param application
     *         information about newly created application
     */
    private void pullSources(final AppInfo application) {
        new PullApplicationSourceHandler()
                .pullApplicationSources(resourceProvider.getVfsId(), project, gitService, new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        String msg = constant.applicationSourcePullingFailed();
                        console.print(msg);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        String msg;
                        if (result) {
                            msg = constant.applicationCreatedSuccessfully(application.getName(), application.getPublicUrl());
                            setProperties();
                        } else {
                            msg = constant.applicationSourcePullingFailed();
                        }
                        console.print(msg);
                    }
                });
    }

    /** Set properties that will be identified current project as OpenShift project to allow user to show OpenShift menu in Project/PaaS. */
    private void setProperties() {
        project.getProperties().add(new Property("openshift-express-application", projectName));
        project.flushProjectProperties(new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Project result) {
                refreshProjectFiles(result);
            }
        });
    }

    /**
     * Refresh updated project and set contents into the project explorer.
     *
     * @param project
     *         project to be updated
     */
    private void refreshProjectFiles(Project project) {
        project.refreshTree(new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Project result) {
                projectExplorer.setContent(result.getParent());
            }
        });
    }
}
