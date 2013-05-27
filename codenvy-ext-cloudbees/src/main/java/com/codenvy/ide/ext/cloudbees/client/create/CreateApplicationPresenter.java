/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.cloudbees.client.create;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.*;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.DomainsUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.ext.jenkins.client.build.BuildApplicationPresenter;
import com.codenvy.ide.ext.jenkins.shared.JobStatus;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.AutoBeanUnmarshallerWS;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

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
    private CloudBeesAutoBeanFactory      autoBeanFactory;
    private LoginPresenter                loginPresenter;
    private CloudBeesClientService        service;
    private BuildApplicationPresenter     buildApplicationPresenter;
    /** Public url to war file of application. */
    private String                        warUrl;
    private String                        projectName;
    private String                        domain;
    private String                        name;
    private Project                       project;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourcesProvider
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     * @param buildApplicationPresenter
     */
    @Inject
    protected CreateApplicationPresenter(CreateApplicationView view, EventBus eventBus, ResourceProvider resourcesProvider,
                                         ConsolePart console, CloudBeesLocalizationConstant constant,
                                         CloudBeesAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter,
                                         CloudBeesClientService service, BuildApplicationPresenter buildApplicationPresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourcesProvider = resourcesProvider;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.buildApplicationPresenter = buildApplicationPresenter;
    }

    /** Shows dialog. */
    public void showDialog() {
        project = resourcesProvider.getActiveProject();
        projectName = project.getName();

        getDomains();
    }

    /** Gets domains. */
    private void getDomains() {
        try {
            DomainsUnmarshaller unmarshaller = new DomainsUnmarshaller(JsonCollections.<String>createArray());
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getDomains();
                }
            };

            service.getDomains(
                    new CloudBeesAsyncRequestCallback<JsonArray<String>>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                         loginPresenter) {
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
            console.print(e.getMessage());
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
        AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();

        try {
            AutoBeanUnmarshallerWS<ApplicationInfo> unmarshaller = new AutoBeanUnmarshallerWS<ApplicationInfo>(autoBean);
            service.initializeApplicationWS(view.getUrl(), resourcesProvider.getVfsId(), project.getId(), warUrl, null,
                                            new CloudBeesRESTfulRequestCallback<ApplicationInfo>(unmarshaller, deployWarLoggedInHandler,
                                                                                                 null, eventBus, console, loginPresenter) {
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
                                                    console.print(constant.deployApplicationFailureMessage());
                                                    super.onFailure(exception);
                                                }
                                            });
        } catch (WebSocketException e) {
            doDeployApplicationREST();
        }
    }

    /** Deploy application to Cloud Bees by sending request over HTTP. */
    private void doDeployApplicationREST() {
        AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();
        try {
            AutoBeanUnmarshaller<ApplicationInfo> unmarshaller = new AutoBeanUnmarshaller<ApplicationInfo>(autoBean);
            service.initializeApplication(view.getUrl(), resourcesProvider.getVfsId(), project.getId(), warUrl, null,
                                          new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, deployWarLoggedInHandler, null,
                                                                                             eventBus, console, loginPresenter) {
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
                                                  console.print(constant.deployApplicationFailureMessage());
                                                  super.onFailure(exception);
                                              }
                                          });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(constant.deployApplicationFailureMessage());
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