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
package com.codenvy.ide.ext.cloudbees.client.wizard;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.*;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.DomainsUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.AutoBeanUnmarshallerWS;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Presenter for creating application on CloudBees from New project wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudBeesPagePresenter extends AbstractWizardPagePresenter implements CloudBeesPageView.ActionDelegate {
    private CloudBeesPageView             view;
    private EventBus                      eventBus;
    private ResourceProvider              resourcesProvider;
    private ConsolePart                   console;
    private CloudBeesLocalizationConstant constant;
    private CloudBeesAutoBeanFactory      autoBeanFactory;
    private HandlerRegistration           projectBuildHandler;
    private LoginPresenter                loginPresenter;
    private CloudBeesClientService        service;
    private TemplateAgent                 templateAgent;
    private CreateProjectProvider         createProjectProvider;
    private boolean                       isLogined;
    /** Public url to war file of application. */
    private String                        warUrl;
    private String                        projectName;
    private String                        domain;
    private String                        name;
    private Project                       project;

    @Inject
    protected CloudBeesPagePresenter(CloudBeesPageView view, EventBus eventBus, ResourceProvider resourcesProvider, ConsolePart console,
                                     CloudBeesLocalizationConstant constant, CloudBeesAutoBeanFactory autoBeanFactory,
                                     LoginPresenter loginPresenter, CloudBeesClientService service, TemplateAgent templateAgent,
                                     CloudBeesResources resources) {
        super("Deploy project to CloudBees", resources.cloudBees48());

        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourcesProvider = resourcesProvider;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.templateAgent = templateAgent;
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String url = view.getDomain() + "/" + view.getName();
        view.setUrl(url);

        delegate.updateControls();
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

    /** Checking entered information on view. */
    public boolean validate() {
        if (isLogined) {
            return view.getName() != null && !view.getName().isEmpty();
        }
        return true;
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
        if (!isLogined) {
            return "This project will be created without deploy on CloudBees.";
        } else if (view.getName().isEmpty()) {
            return "Please, enter a application's name.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        createProjectProvider = templateAgent.getSelectedTemplate().getCreateProjectProvider();
        projectName = createProjectProvider.getProjectName();

        isLogined = true;
        getDomains();

        container.setWidget(view);
    }

    /** Gets domains. */
    private void getDomains() {
        try {
            DomainsUnmarshaller unmarshaller = new DomainsUnmarshaller(JsonCollections.<String>createArray());
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    isLogined = true;
                    getDomains();
                }
            };
            LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
                @Override
                public void onLoginCanceled() {
                    isLogined = false;
                    delegate.updateControls();
                }
            };

            service.getDomains(
                    new CloudBeesAsyncRequestCallback<JsonArray<String>>(unmarshaller, loggedInHandler, loginCanceledHandler, eventBus,
                                                                         console, loginPresenter) {
                        @Override
                        protected void onSuccess(JsonArray<String> result) {
                            view.setDomainValues(result);
                            domain = view.getDomain();
                            view.setName(projectName);
                            name = view.getName();
                            view.setUrl(domain + "/" + name);
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                project = result;
                // TODO
//                if (isLogined) {
//                    getFirstDeployDomains();
////                    buildApplication();
//                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CloudBeesPagePresenter.class, caught);
            }
        });
    }

    private void beforeDeploy() {
        JsonArray<Resource> children = project.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.isFile() && "pom.xml".equals(child.getName())) {
                buildApplication();
                return;
            }
        }

        createApplication();
    }

    /** Create application on Cloud Bees by sending request over WebSocket or HTTP. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };
        // TODO Need to create some special service after this class
        // This class still doesn't have analog.
        //        JobManager.get().showJobSeparated();
        AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();

        try {
            AutoBeanUnmarshallerWS<ApplicationInfo> unmarshaller =
                    new AutoBeanUnmarshallerWS<ApplicationInfo>(autoBean);

            service.initializeApplicationWS(view.getUrl(), resourcesProvider.getVfsId(), project.getId(), warUrl, null,
                                            new CloudBeesRESTfulRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null,
                                                                                                 eventBus, console, loginPresenter) {
                                                @Override
                                                protected void onSuccess(final ApplicationInfo appInfo) {
                                                    project.refreshProperties(new AsyncCallback<Project>() {
                                                        @Override
                                                        public void onSuccess(Project project) {
                                                            onCreatedSuccess(appInfo);
                                                            eventBus.fireEvent(new RefreshBrowserEvent(project));
                                                        }

                                                        @Override
                                                        public void onFailure(Throwable caught) {
                                                            Log.error(CloudBeesPagePresenter.class, "Can not refresh properties", caught);
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
            createApplicationREST(loggedInHandler);
        }
    }

    /** Create application on Cloud Bees by sending request over HTTP. */
    private void createApplicationREST(LoggedInHandler loggedInHandler) {
        try {
            AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();
            AutoBeanUnmarshaller<ApplicationInfo> unmarshaller = new AutoBeanUnmarshaller<ApplicationInfo>(autoBean);

            service.initializeApplication(view.getUrl(), resourcesProvider.getVfsId(), project.getId(), warUrl, null,
                                          new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                             console, loginPresenter) {
                                              @Override
                                              protected void onSuccess(final ApplicationInfo appInfo) {
                                                  project.refreshProperties(new AsyncCallback<Project>() {
                                                      @Override
                                                      public void onSuccess(Project project) {
                                                          onCreatedSuccess(appInfo);
                                                          eventBus.fireEvent(new RefreshBrowserEvent(project));
                                                      }

                                                      @Override
                                                      public void onFailure(Throwable caught) {
                                                          Log.error(CloudBeesPagePresenter.class, "Can not refresh properties", caught);
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
            console.print(constant.deployApplicationFailureMessage());
        }
    }

    private void onCreatedSuccess(ApplicationInfo appInfo) {
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

    private void getFirstDeployDomains() {
        try {
            DomainsUnmarshaller unmarshaller = new DomainsUnmarshaller(JsonCollections.<String>createArray());
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    isLogined = true;
                    getFirstDeployDomains();
                }
            };

            LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
                @Override
                public void onLoginCanceled() {
                    isLogined = false;
                    delegate.updateControls();
                }
            };

            service.getDomains(
                    new CloudBeesAsyncRequestCallback<JsonArray<String>>(unmarshaller, loggedInHandler, loginCanceledHandler, eventBus,
                                                                         console, loginPresenter) {
                        @Override
                        protected void onSuccess(JsonArray<String> result) {
                            view.setDomainValues(result);
                            domain = view.getDomain();
                            view.setName(projectName);
                            name = view.getName();
                            view.setUrl(domain + "/" + name);
                            buildApplication();
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Builds application. */
    private void buildApplication() {
        // TODO


    }
}