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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.rebuild;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter that allow user to rebuild instance.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class RebuildEnvironmentPresenter implements RebuildEnvironmentView.ActionDelegate {
    private RebuildEnvironmentView         view;
    private EventBus                       eventBus;
    private BeanstalkClientService         service;
    private EnvironmentInfo                environmentInfo;
    private AWSLocalizationConstant        constant;
    private LoginPresenter                 loginPresenter;
    private NotificationManager            notificationManager;
    private AsyncCallback<EnvironmentInfo> callback;

    /**
     * Create view.
     *
     * @param view
     * @param eventBus
     * @param service
     * @param constant
     * @param loginPresenter
     * @param notificationManager
     */
    @Inject
    public RebuildEnvironmentPresenter(RebuildEnvironmentView view, EventBus eventBus, BeanstalkClientService service,
                                       AWSLocalizationConstant constant, LoginPresenter loginPresenter,
                                       NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog(EnvironmentInfo environmentInfo, AsyncCallback<EnvironmentInfo> callback) {
        this.callback = callback;

        this.environmentInfo = environmentInfo;
        if (!environmentInfo.getStatus().equals(EnvironmentStatus.Ready)) {
            Window.alert("Environment is in an invalid state for this operation. Must be Ready");
            return;
        }

        if (!view.isShown()) {
            view.showDialog();
        }

        view.setRebuildQuestion(constant.rebuildEnvironmentQuestion(environmentInfo.getName()));
    }

    /** {@inheritDoc} */
    @Override
    public void onRebuildButtonClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onRebuildButtonClicked();
            }
        };

        try {
            service.rebuildEnvironment(environmentInfo.getId(),
                                       new AwsAsyncRequestCallback<Object>(null, loggedInHandler, null, loginPresenter) {
                                           @Override
                                           protected void processFail(Throwable exception) {
                                               String message = constant.rebuildEnvironmentFailed(environmentInfo.getId());
                                               if (exception instanceof ServerException &&
                                                   exception.getMessage() != null) {
                                                   message += "<br>" + exception.getMessage();
                                               }

                                               Notification notification = new Notification(message, ERROR);
                                               notificationManager.showNotification(notification);
                                           }

                                           @Override
                                           protected void onSuccess(Object result) {
                                               view.close();
                                               if (callback != null) {
                                                   callback.onSuccess(environmentInfo);
                                               }
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
    public void onCancelButtonClicked() {
        view.close();
    }
}
