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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.terminate;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentStatus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter that allow user to terminate instance.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class TerminateEnvironmentPresenter implements TerminateEnvironmentView.ActionDelegate {
    private TerminateEnvironmentView       view;
    private EventBus                       eventBus;
    private ConsolePart                    console;
    private AWSLocalizationConstant        constant;
    private BeanstalkClientService         service;
    private LoginPresenter                 loginPresenter;
    private EnvironmentInfo                environmentInfo;
    private AsyncCallback<EnvironmentInfo> callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param service
     * @param loginPresenter
     */
    @Inject
    public TerminateEnvironmentPresenter(TerminateEnvironmentView view, EventBus eventBus, ConsolePart console,
                                         AWSLocalizationConstant constant,
                                         BeanstalkClientService service, LoginPresenter loginPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;

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

        view.setTerminateQuestion(constant.terminateEnvironmentQuestion(environmentInfo.getName()));
    }

    /** {@inheritDoc} */
    @Override
    public void onTerminateButtonClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onTerminateButtonClicked();
            }
        };

        DtoClientImpls.EnvironmentInfoImpl dtoEnvironmentInfo = DtoClientImpls.EnvironmentInfoImpl.make();
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller(dtoEnvironmentInfo);

        try {
            service.stopEnvironment(environmentInfo.getId(),
                                    new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                        @Override
                                        protected void processFail(Throwable exception) {
                                            String message = constant.rebuildEnvironmentFailed(environmentInfo.getId());
                                            if (exception instanceof ServerException &&
                                                exception.getMessage() != null) {
                                                message += "<br>" + exception.getMessage();
                                            }

                                            console.print(message);
                                        }

                                        @Override
                                        protected void onSuccess(EnvironmentInfo result) {
                                            view.close();
                                            if (callback != null) {
                                                callback.onSuccess(environmentInfo);
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
