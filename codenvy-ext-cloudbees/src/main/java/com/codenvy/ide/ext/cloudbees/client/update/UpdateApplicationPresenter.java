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
package com.codenvy.ide.ext.cloudbees.client.update;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAutoBeanFactory;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.ext.jenkins.client.build.BuildApplicationPresenter;
import com.codenvy.ide.ext.jenkins.shared.JobStatus;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for updating application on CloudBees.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateApplicationPresenter.java Oct 10, 2011 5:07:40 PM vereshchaka $
 */
@Singleton
public class UpdateApplicationPresenter {
    /** Location of war file (Java only). */
    private String                        warUrl;
    private EventBus                      eventBus;
    private ResourceProvider              resourceProvider;
    private ConsolePart                   console;
    private CloudBeesLocalizationConstant constant;
    private CloudBeesAutoBeanFactory      autoBeanFactory;
    private LoginPresenter                loginPresenter;
    private CloudBeesClientService        service;
    private BuildApplicationPresenter     buildApplicationPresenter;
    /** Message for git commit. */
    private String                        updateMessage;
    private Project                       project;
    private String                        appId;
    private String                        appTitle;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     * @param buildApplicationPresenter
     */
    @Inject
    protected UpdateApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                         CloudBeesLocalizationConstant constant, CloudBeesAutoBeanFactory autoBeanFactory,
                                         LoginPresenter loginPresenter, CloudBeesClientService service,
                                         BuildApplicationPresenter buildApplicationPresenter) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.buildApplicationPresenter = buildApplicationPresenter;
    }

    /** Updates CloudBees application. */
    public void updateApp(String id, String title) {
        this.project = resourceProvider.getActiveProject();
        appId = id;
        appTitle = title;

        if (appId != null && appTitle != null) {
            askForMessage();
        } else if (project != null) {
            getApplicationInfo();
        }
    }

    /** Shows update message. */
    private void askForMessage() {
        updateMessage = Window.prompt(constant.updateAppAskForMsgText(), "");
        buildApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        buildApplicationPresenter.build(project, new AsyncCallback<JobStatus>() {
            @Override
            public void onSuccess(JobStatus result) {
                if (result.getArtifactUrl() != null) {
                    warUrl = result.getArtifactUrl();
                    doUpdate();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(UpdateApplicationPresenter.class, "Can not build project on Jenkins", caught);
            }
        });
    }

    /** Updates application. */
    private void doUpdate() {
        String projectId = null;
        if (project != null) {
            projectId = project.getId();
        }

        try {
            AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();
            AutoBeanUnmarshaller<ApplicationInfo> unmarshaller = new AutoBeanUnmarshaller<ApplicationInfo>(autoBean);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    doUpdate();
                }
            };

            service.updateApplication(appId, resourceProvider.getVfsId(), projectId, warUrl, updateMessage,
                                      new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                         console, loginPresenter) {
                                          @Override
                                          protected void onSuccess(ApplicationInfo appInfo) {
                                              console.print(constant.applicationUpdatedMsg(appTitle));
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Get information about application. */
    protected void getApplicationInfo() {
        String projectId = project.getId();

        try {
            AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();
            AutoBeanUnmarshaller<ApplicationInfo> unmarshaller = new AutoBeanUnmarshaller<ApplicationInfo>(autoBean);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getApplicationInfo();
                }
            };

            service.getApplicationInfo(null, resourceProvider.getVfsId(), projectId,
                                       new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                          console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(ApplicationInfo appInfo) {
                                               appId = appInfo.getId();
                                               appTitle = appInfo.getTitle();
                                               askForMessage();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}