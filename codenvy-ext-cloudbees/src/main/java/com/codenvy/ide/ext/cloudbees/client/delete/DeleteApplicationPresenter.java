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
package com.codenvy.ide.ext.cloudbees.client.delete;

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
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for deleting application from CloudBees. Performs following actions on delete: 1. Gets application id (application
 * info) by work dir (location on file system). 2. Asks user to confirm the deleting of the application. 3. When user confirms -
 * performs deleting the application.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 1, 2011 12:59:52 PM vereshchaka $
 */
@Singleton
public class DeleteApplicationPresenter {
    private ResourceProvider              resourceProvider;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private CloudBeesLocalizationConstant constant;
    private CloudBeesAutoBeanFactory      autoBeanFactory;
    private LoginPresenter                loginPresenter;
    private AsyncCallback<String>         appDeleteCallback;
    private CloudBeesClientService        service;

    /**
     * Create presenter.
     *
     * @param resourceProvider
     * @param eventBus
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected DeleteApplicationPresenter(ResourceProvider resourceProvider, EventBus eventBus, ConsolePart console,
                                         CloudBeesLocalizationConstant constant, CloudBeesAutoBeanFactory autoBeanFactory,
                                         LoginPresenter loginPresenter, CloudBeesClientService service) {
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /**
     * Deletes CloudBees application.
     *
     * @param id
     * @param title
     * @param callback
     */
    public void deleteApp(String id, String title, AsyncCallback<String> callback) {
        this.appDeleteCallback = callback;

        if (id != null && title != null) {
            String appTitle = title != null ? title : id;
            askForDelete(id, appTitle);
        } else {
            getApplicationInfo();
        }
    }

    /** Get information about application. */
    private void getApplicationInfo() {
        Project project = resourceProvider.getActiveProject();
        if (project != null) {
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
                                                   askForDelete(appInfo.getId(), appInfo.getTitle());
                                               }
                                           });
            } catch (RequestException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(e));
                console.print(e.getMessage());
            }
        }
    }

    /** Show confirmation message before delete. */
    private void askForDelete(final String appId, final String appTitle) {
        if (Window.confirm(constant.deleteApplicationQuestion(appTitle))) {
            doDelete(appId, appTitle, appDeleteCallback);
        }
    }

    /**
     * Deletes application.
     *
     * @param appId
     * @param appTitle
     * @param callback
     */
    private void doDelete(final String appId, final String appTitle, final AsyncCallback<String> callback) {
        String projectId = null;
        final Project project = resourceProvider.getActiveProject();

        if (project != null && project.getPropertyValue("cloudbees-application") != null
            && appId.equals(project.getPropertyValue("cloudbees-application"))) {
            projectId = project.getId();
        }

        try {
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    doDelete(appId, appTitle, callback);
                }
            };

            service.deleteApplication(appId, resourceProvider.getVfsId(), projectId,
                                      new CloudBeesAsyncRequestCallback<String>(loggedInHandler, null, eventBus, console, loginPresenter) {
                                          @Override
                                          protected void onSuccess(final String result) {
                                              if (project != null) {
                                                  project.refreshProperties(new AsyncCallback<Project>() {
                                                      @Override
                                                      public void onSuccess(Project project) {
                                                          console.print(constant.applicationDeletedMsg(appTitle));
                                                          callback.onSuccess(result);
                                                      }

                                                      @Override
                                                      public void onFailure(Throwable caught) {
                                                          callback.onFailure(caught);
                                                      }
                                                  });
                                              } else {
                                                  console.print(constant.applicationDeletedMsg(appTitle));
                                                  callback.onSuccess(result);
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            console.print(constant.applicationDeletedMsg(appTitle));
            appDeleteCallback.onFailure(e);
        }
    }
}