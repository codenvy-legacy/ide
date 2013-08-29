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
package com.codenvy.ide.extension.cloudfoundry.client.delete;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for delete application operation.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
@Singleton
public class DeleteApplicationPresenter implements DeleteApplicationView.ActionDelegate {
    private DeleteApplicationView               view;
    /** The name of application. */
    private String                              appName;
    /** Name of the server. */
    private String                              serverName;
    private ResourceProvider                    resourceProvider;
    private EventBus                            eventBus;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private LoginPresenter                      loginPresenter;
    private AsyncCallback<String>               appDeleteCallback;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create presenter.
     *
     * @param view
     * @param resourceProvider
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected DeleteApplicationPresenter(DeleteApplicationView view, ResourceProvider resourceProvider, EventBus eventBus,
                                         ConsolePart console, CloudFoundryLocalizationConstant constant,
                                         LoginPresenter loginPresenter, CloudFoundryClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        deleteApplication(appDeleteCallback);
    }

    /**
     * Deletes CloudFoundry application.
     *
     * @param appName
     * @param serverName
     * @param paasProvider
     * @param callback
     */
    public void deleteApp(String appName, String serverName, CloudFoundryExtension.PAAS_PROVIDER paasProvider,
                          AsyncCallback<String> callback) {
        this.serverName = serverName;
        this.appDeleteCallback = callback;
        this.paasProvider = paasProvider;

        // If application name is absent then need to find it
        if (appName == null) {
            getApplicationInfo();
        } else {
            this.appName = appName;
            showDialog(appName);
        }
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    /** Get application's name and put it to the field. */
    private void getApplicationInfo() {
        String projectId = resourceProvider.getActiveProject().getId();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, appInfoLoggedInHandler,
                                                                                                     null, eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
                                               appName = result.getName();
                                               showDialog(appName);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            deleteApplication(appDeleteCallback);
        }
    };

    /**
     * Deletes application.
     *
     * @param callback
     */
    private void deleteApplication(final AsyncCallback<String> callback) {
        boolean isDeleteServices = view.isDeleteServices();
        String projectId = null;

        final Project project = resourceProvider.getActiveProject();
        // Checking does current project work with deleting CloudFoundry application.
        // If project don't have the same CloudFoundry application name in properties
        // then this property won't be cleaned.
        if (project != null) {
            final boolean isCloudFoundryApp = paasProvider == CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY &&
                                              project.getPropertyValue("cloudfoundry-application") != null
                                              && appName.equals(project.getPropertyValue("cloudfoundry-application"));

            final boolean isWebFabricApp = paasProvider == CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC &&
                                           project.getPropertyValue("tier3webfabric-application") != null
                                           && appName.equals(project.getPropertyValue("tier3webfabric-application"));

            if (isCloudFoundryApp || isWebFabricApp) {
                projectId = project.getId();
            }
        }

        try {
            service.deleteApplication(resourceProvider.getVfsId(), projectId, appName, serverName, paasProvider, isDeleteServices,
                                      new CloudFoundryAsyncRequestCallback<String>(null, deleteAppLoggedInHandler, null, eventBus,
                                                                                   console, constant, loginPresenter, paasProvider) {
                                          @Override
                                          protected void onSuccess(final String result) {
                                              if (project != null) {
                                                  project.refreshProperties(new AsyncCallback<Project>() {
                                                      @Override
                                                      public void onSuccess(Project project) {
                                                          view.close();
                                                          console.print(constant.applicationDeletedMsg(appName));

                                                          callback.onSuccess(result);
                                                      }

                                                      @Override
                                                      public void onFailure(Throwable caught) {
                                                          callback.onFailure(caught);
                                                      }
                                                  });
                                              } else {
                                                  view.close();
                                                  console.print(constant.applicationDeletedMsg(appName));

                                                  callback.onSuccess(result);
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Shows dialog.
     *
     * @param appName
     *         application name which need to delete
     */
    private void showDialog(String appName) {
        view.setAskMessage(constant.deleteApplicationQuestion(appName));
        view.setDeleteServices(false);

        view.showDialog();
    }
}