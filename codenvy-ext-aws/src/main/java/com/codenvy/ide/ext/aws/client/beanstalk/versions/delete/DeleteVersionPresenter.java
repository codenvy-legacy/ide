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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.delete;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter to allow user delete beanstalk application.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class DeleteVersionPresenter implements DeleteVersionView.ActionDelegate {
    private DeleteVersionView                     view;
    private EventBus                              eventBus;
    private ConsolePart                           console;
    private BeanstalkClientService                service;
    private LoginPresenter                        loginPresenter;
    private AWSLocalizationConstant               constant;
    private ApplicationVersionInfo                version;
    private ResourceProvider                      resourceProvider;
    private AsyncCallback<ApplicationVersionInfo> callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param loginPresenter
     * @param constant
     * @param resourceProvider
     */
    @Inject
    public DeleteVersionPresenter(DeleteVersionView view, EventBus eventBus, ConsolePart console,
                                  BeanstalkClientService service, LoginPresenter loginPresenter,
                                  AWSLocalizationConstant constant, ResourceProvider resourceProvider) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.constant = constant;
        this.resourceProvider = resourceProvider;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog(ApplicationVersionInfo version, AsyncCallback<ApplicationVersionInfo> callback) {
        this.version = version;
        this.callback = callback;

        if (!view.isShown()) {
            view.showDialog();
        }

        view.setDeleteQuestion(constant.deleteVersionQuestion(version.getVersionLabel()));
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteButtonCLicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteButtonCLicked();
            }
        };

        try {
            service.deleteVersion(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(), version.getApplicationName(),
                                  version.getVersionLabel(), view.getDeleteS3Bundle(),
                                  new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                      @Override
                                      protected void processFail(Throwable exception) {
                                          String message = constant.deleteVersionFailed(version.getVersionLabel());
                                          if (exception instanceof ServerException && exception.getMessage() != null) {
                                              message += "<br>" + exception.getMessage();
                                          }

                                          console.print(message);

                                          if (callback != null) {
                                              callback.onSuccess(null);
                                          }
                                      }

                                      @Override
                                      protected void onSuccess(Object result) {
                                          view.close();

                                          if (callback != null) {
                                              callback.onSuccess(version);
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
    public void onCancelButtonClicked() {
        view.close();
    }
}
