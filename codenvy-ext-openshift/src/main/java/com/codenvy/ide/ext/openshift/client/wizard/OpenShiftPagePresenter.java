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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.openshift.client.*;
import com.codenvy.ide.ext.openshift.client.key.UpdateKeyPresenter;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.client.marshaller.ApplicationInfoUnmarshallerWS;
import com.codenvy.ide.ext.openshift.client.marshaller.ListUnmarshaller;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.ext.openshift.client.OpenShiftExtension.ID;
import static com.codenvy.ide.json.JsonCollections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/**
 * Wizard page for creating project on OpenShift.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class OpenShiftPagePresenter extends AbstractPaasPage implements OpenShiftPageView.ActionDelegate {
    private OpenShiftPageView             view;
    private EventBus                      eventBus;
    private ResourceProvider              resourceProvider;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private OpenShiftClientService        service;
    private UpdateKeyPresenter            updateKeyPresenter;
    private GitClientService              gitService;
    private ProjectExplorerPartPresenter  projectExplorer;
    private NotificationManager           notificationManager;
    private boolean                       isLogged;
    private Project                       project;
    private String                        projectName;
    private Notification                  notification;
    private CommitCallback                callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param loginPresenter
     * @param service
     * @param resources
     * @param updateKeyPresenter
     * @param gitService
     * @param projectExplorer
     */
    @Inject
    protected OpenShiftPagePresenter(OpenShiftPageView view, EventBus eventBus, ResourceProvider resourceProvider,
                                     OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter, OpenShiftClientService service,
                                     OpenShiftResources resources, UpdateKeyPresenter updateKeyPresenter, GitClientService gitService,
                                     ProjectExplorerPartPresenter projectExplorer, NotificationManager notificationManager) {
        super("Deploy project to OpenShift", resources.openShift48(), ID);

        this.view = view;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.updateKeyPresenter = updateKeyPresenter;
        this.gitService = gitService;
        this.projectExplorer = projectExplorer;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onApplicationNameChanged() {
        projectName = view.getName();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        //do nothing
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
        projectName = wizardContext.getData(PROJECT_NAME);
        view.setName(projectName);

        getApplicationTypes();

        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull CommitCallback callback) {
        this.callback = callback;

        if (!isLogged) {
            callback.onSuccess();
            return;
        }

        notification = new Notification(constant.creatingApplicationStarted(projectName), PROGRESS);
        notificationManager.showNotification(notification);

        projectExplorer.setContent(null);

        JsonArray<Property> properties = createArray(new Property(PROPERTY_PRIMARY_NATURE, PRIMARY_NATURE));

        // TODO IDEX-181 Exception happens into createApplication
        resourceProvider.createProject(projectName, properties, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                project = result;
                createApplication();
            }

            @Override
            public void onFailure(Throwable caught) {
                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage(caught.getMessage());
                OpenShiftPagePresenter.this.callback.onFailure(caught);
            }
        });
    }

    /** Get application types supported by OpenShift. */
    private void getApplicationTypes() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationTypes();
            }
        };
        ListUnmarshaller unmarshaller = new ListUnmarshaller();

        try {
            service.getApplicationTypes(
                    new OpenShiftAsyncRequestCallback<JsonArray<String>>(unmarshaller, loggedInHandler, null, eventBus, loginPresenter,
                                                                         notificationManager) {
                        @Override
                        protected void onSuccess(JsonArray<String> result) {
                            isLogged = true;
                            view.setApplicationTypes(result);
                        }
                    });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
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

    /** Create application on OpenShift and after that start to update public key. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };
        ApplicationInfoUnmarshallerWS unmarshaller = new ApplicationInfoUnmarshallerWS();

        try {
            service.createApplicationWS(projectName, resourceProvider.getVfsId(), project.getId(), view.getApplicationType(),
                                        view.getScalingValue(),
                                        new OpenShiftWSRequestCallback<AppInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                loginPresenter, notificationManager) {
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
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            callback.onFailure(e);
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
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();

        try {
            service.createApplication(projectName, resourceProvider.getVfsId(), project.getId(), view.getApplicationType(),
                                      view.getScalingValue(),
                                      new OpenShiftAsyncRequestCallback<AppInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                 loginPresenter, notificationManager) {
                                          @Override
                                          protected void onSuccess(AppInfo result) {
                                              updatePublicKey(result);
                                          }

                                          @Override
                                          protected void onFailure(Throwable exception) {
                                              super.onFailure(exception);
                                              notification.setStatus(FINISHED);
                                              notification.setType(ERROR);
                                              notification.setMessage(exception.getMessage());
                                              callback.onFailure(exception);
                                              //TODO cleanup project if creation of application on OpenShift is failed.
                                          }
                                      });
        } catch (RequestException e) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            callback.onFailure(e);
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
                notification.setStatus(FINISHED);
                notification.setType(ERROR);
                notification.setMessage(msg);
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    pullSources(application);
                } else {
                    String msg = constant.applicationPublicKeyUpdateFailed();
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(msg);
                    callback.onSuccess();
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
                        notification.setStatus(FINISHED);
                        notification.setType(ERROR);
                        notification.setMessage(msg);
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        String msg;
                        if (result) {
                            msg = constant.applicationCreatedSuccessfully(application.getName(), application.getPublicUrl());
                            notification.setStatus(FINISHED);
                            notification.setMessage(msg);
                            setProperties();
                        } else {
                            msg = constant.applicationSourcePullingFailed();
                            notification.setStatus(FINISHED);
                            notification.setType(ERROR);
                            notification.setMessage(msg);
                            callback.onSuccess();
                        }
                    }
                });
    }

    /** Set properties that will be identified current project as OpenShift project to allow user to show OpenShift menu in Project/PaaS. */
    private void setProperties() {
        project.getProperties().add(new Property("openshift-express-application", projectName));
        project.flushProjectProperties(new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
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
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Project result) {
                projectExplorer.setContent(result.getParent());
                callback.onSuccess();
            }
        });
    }
}
