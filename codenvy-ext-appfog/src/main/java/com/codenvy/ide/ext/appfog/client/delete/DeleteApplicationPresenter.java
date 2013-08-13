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
package com.codenvy.ide.ext.appfog.client.delete;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for delete application operation.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class DeleteApplicationPresenter implements DeleteApplicationView.ActionDelegate {
    private DeleteApplicationView      view;
    /** The name of application. */
    private String                     appName;
    /** Name of the server. */
    private String                     serverName;
    private ResourceProvider           resourceProvider;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private AsyncCallback<String>      appDeleteCallback;
    private AppfogClientService        service;

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
                                         ConsolePart console, AppfogLocalizationConstant constant, LoginPresenter loginPresenter,
                                         AppfogClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            deleteApplication(appName, serverName, appDeleteCallback);
        }
    };

    /**
     * Deletes AppFog application.
     *
     * @param appName
     * @param serverName
     * @param callback
     */
    public void deleteApp(String appName, String serverName, AsyncCallback<String> callback) {
        this.appName = appName;
        this.serverName = serverName;
        this.appDeleteCallback = callback;

        // If application name is absent then need to find it
        if (appName == null) {
            getApplicationInfo();
        } else {
            this.appName = appName;
            showDialog(appName);
        }
    }

    /** Get application's name and put it to the field. */
    private void getApplicationInfo() {
        String projectId = resourceProvider.getActiveProject().getId();
        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, appInfoLoggedInHandler, null,
                                                                                         eventBus, constant, console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               appName = result.getName();
                                               showDialog(appName);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Deletes application.
     *
     * @param appName
     * @param serverName
     * @param callback
     */
    private void deleteApplication(final String appName, String serverName, final AsyncCallback<String> callback) {
        boolean isDeleteServices = view.isDeleteServices();
        String projectId = null;
        final Project project = resourceProvider.getActiveProject();

        if (project != null && project.getPropertyValue("appfog-application") != null &&
            appName.equals(project.getPropertyValue("appfog-application"))) {
            projectId = project.getId();
        }

        try {
            service.deleteApplication(resourceProvider.getVfsId(), projectId, appName, serverName, isDeleteServices,
                                      new AppfogAsyncRequestCallback<String>(null, deleteAppLoggedInHandler, null, eventBus, constant,
                                                                             console, loginPresenter) {
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

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        deleteApplication(appName, serverName, appDeleteCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
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