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
package com.codenvy.ide.ext.cloudbees.client.delete;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.cloudbees.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected DeleteApplicationPresenter(ResourceProvider resourceProvider, EventBus eventBus, ConsolePart console,
                                         CloudBeesLocalizationConstant constant, LoginPresenter loginPresenter,
                                         CloudBeesClientService service) {
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
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

            DtoClientImpls.ApplicationInfoImpl applicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
            ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(applicationInfo);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getApplicationInfo();
                }
            };

            try {
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