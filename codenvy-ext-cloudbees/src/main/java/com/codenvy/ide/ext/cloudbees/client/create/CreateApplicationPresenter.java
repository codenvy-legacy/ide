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
package com.codenvy.ide.ext.cloudbees.client.create;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesRESTfulRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationInfoUnmarshallerWS;
import com.codenvy.ide.ext.cloudbees.client.marshaller.DomainsUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.ext.jenkins.client.build.BuildApplicationPresenter;
import com.codenvy.ide.ext.jenkins.shared.JobStatus;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for creating application on CloudBees.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationPresenter.java Jun 23, 2011 12:49:09 PM vereshchaka $
 */
@Singleton
public class CreateApplicationPresenter implements CreateApplicationView.ActionDelegate {
    private CreateApplicationView         view;
    private EventBus                      eventBus;
    private ResourceProvider              resourcesProvider;
    private ConsolePart                   console;
    private CloudBeesLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private CloudBeesClientService        service;
    private BuildApplicationPresenter     buildApplicationPresenter;
    private NotificationManager           notificationManager;
    /** Public url to war file of application. */
    private String                        warUrl;
    private String                        projectName;
    private String                        domain;
    private String                        name;
    private Project                       project;
    private Notification                  notification;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourcesProvider
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     * @param buildApplicationPresenter
     * @param notificationManager
     */
    @Inject
    protected CreateApplicationPresenter(CreateApplicationView view, EventBus eventBus, ResourceProvider resourcesProvider,
                                         ConsolePart console, CloudBeesLocalizationConstant constant, LoginPresenter loginPresenter,
                                         CloudBeesClientService service, BuildApplicationPresenter buildApplicationPresenter,
                                         NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourcesProvider = resourcesProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.buildApplicationPresenter = buildApplicationPresenter;
        this.notificationManager = notificationManager;
    }

    /** Shows dialog. */
    public void showDialog() {
        project = resourcesProvider.getActiveProject();
        projectName = project.getName();

        getDomains();
    }

    /** Gets domains. */
    private void getDomains() {
        DomainsUnmarshaller unmarshaller = new DomainsUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getDomains();
            }
        };

        try {
            service.getDomains(
                    new CloudBeesAsyncRequestCallback<JsonArray<String>>(unmarshaller, loggedInHandler, null, eventBus,
                                                                         loginPresenter, notificationManager) {
                        @Override
                        protected void onSuccess(JsonArray<String> result) {
                            view.setDomainValues(result);
                            domain = view.getDomain();
                            view.setName(projectName);
                            name = view.getName();
                            view.setUrl(domain + "/" + name);

                            view.showDialog();
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        buildApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        buildApplicationPresenter.build(project, new AsyncCallback<JobStatus>() {
            @Override
            public void onSuccess(JobStatus result) {
                if (result.getArtifactUrl() != null) {
                    warUrl = result.getArtifactUrl();
                    doDeployApplication();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CreateApplicationPresenter.class, "Can not build project on Jenkins", caught);
            }
        });

        view.close();
    }

    /** If user is not logged in to CloudBees, this handler will be called, after user logged in. */
    private LoggedInHandler deployWarLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            doDeployApplication();
        }
    };

    /** Deploy application to Cloud Bees by sending request over WebSocket or HTTP. */
    private void doDeployApplication() {
        ApplicationInfoUnmarshallerWS unmarshaller = new ApplicationInfoUnmarshallerWS();
        notification = new Notification(constant.creatingApplication(), PROGRESS);
        notificationManager.showNotification(notification);

        try {
            service.initializeApplicationWS(view.getUrl(), resourcesProvider.getVfsId(), project.getId(), warUrl, null,
                                            new CloudBeesRESTfulRequestCallback<ApplicationInfo>(unmarshaller, deployWarLoggedInHandler,
                                                                                                 null, eventBus, loginPresenter,
                                                                                                 notificationManager) {
                                                @Override
                                                protected void onSuccess(final ApplicationInfo appInfo) {
                                                    project.refreshProperties(new AsyncCallback<Project>() {
                                                        @Override
                                                        public void onSuccess(Project project) {
                                                            onDeploySuccess(appInfo);
                                                            eventBus.fireEvent(new RefreshBrowserEvent(project));
                                                        }

                                                        @Override
                                                        public void onFailure(Throwable caught) {
                                                            Log.error(CreateApplicationPresenter.class, "Can not refresh properties",
                                                                      caught);
                                                        }
                                                    });
                                                }

                                                @Override
                                                protected void onFailure(Throwable exception) {
                                                    notification.setStatus(FINISHED);
                                                    notification.setType(ERROR);
                                                    notification.setMessage(constant.deployApplicationFailureMessage());
                                                    super.onFailure(exception);
                                                }
                                            });
        } catch (WebSocketException e) {
            doDeployApplicationREST();
        }
    }

    /** Deploy application to Cloud Bees by sending request over HTTP. */
    private void doDeployApplicationREST() {
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();

        try {
            service.initializeApplication(view.getUrl(), resourcesProvider.getVfsId(), project.getId(), warUrl, null,
                                          new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, deployWarLoggedInHandler, null,
                                                                                             eventBus, loginPresenter,
                                                                                             notificationManager) {
                                              @Override
                                              protected void onSuccess(final ApplicationInfo appInfo) {
                                                  project.refreshProperties(new AsyncCallback<Project>() {
                                                      @Override
                                                      public void onSuccess(Project project) {
                                                          onDeploySuccess(appInfo);
                                                          eventBus.fireEvent(new RefreshBrowserEvent(project));
                                                      }

                                                      @Override
                                                      public void onFailure(Throwable caught) {
                                                          Log.error(CreateApplicationPresenter.class, "Can not refresh properties",
                                                                    caught);
                                                      }
                                                  });
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  notification.setStatus(FINISHED);
                                                  notification.setType(ERROR);
                                                  notification.setMessage(constant.deployApplicationFailureMessage());
                                                  super.onFailure(exception);
                                              }
                                          });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(constant.deployApplicationFailureMessage());
        }
    }

    /**
     * Shows information about Success application build.
     *
     * @param appInfo
     */
    private void onDeploySuccess(ApplicationInfo appInfo) {
        StringBuilder output = new StringBuilder(constant.deployApplicationSuccess()).append("<br>");
        output.append(constant.deployApplicationInfo()).append("<br>");
        output.append(constant.applicationInfoListGridId()).append(" : ").append(appInfo.getId()).append("<br>");
        output.append(constant.applicationInfoListGridTitle()).append(" : ").append(appInfo.getTitle()).append("<br>");
        output.append(constant.applicationInfoListGridServerPool()).append(" : ").append(appInfo.getServerPool()).append("<br>");
        output.append(constant.applicationInfoListGridStatus()).append(" : ").append(appInfo.getStatus()).append("<br>");
        output.append(constant.applicationInfoListGridContainer()).append(" : ").append(appInfo.getContainer()).append("<br>");
        output.append(constant.applicationInfoListGridIdleTimeout()).append(" : ").append(appInfo.getIdleTimeout()).append("<br>");
        output.append(constant.applicationInfoListGridMaxMemory()).append(" : ").append(appInfo.getMaxMemory()).append("<br>");
        output.append(constant.applicationInfoListGridSecurityMode()).append(" : ").append(appInfo.getSecurityMode()).append("<br>");
        output.append(constant.applicationInfoListGridClusterSize()).append(" : ").append(appInfo.getClusterSize()).append("<br>");
        output.append(constant.applicationInfoListGridUrl()).append(" : ").append("<a href='").append(appInfo.getUrl())
              .append("' target='_blank'>").append(appInfo.getUrl()).append("</a>").append("<br>");

        console.print(output.toString());
        notification.setStatus(FINISHED);
        notification.setMessage(constant.deployApplicationSuccess());
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        domain = view.getDomain();
        name = view.getName();
        view.setUrl(domain + "/" + name);

        view.setEnableCreateButton(validate());
    }

    /** Checking entered information on view. */
    private boolean validate() {
        return view.getName() != null && !view.getName().isEmpty();
    }
}