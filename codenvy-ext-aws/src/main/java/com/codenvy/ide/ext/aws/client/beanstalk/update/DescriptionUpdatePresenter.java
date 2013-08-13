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
package com.codenvy.ide.ext.aws.client.beanstalk.update;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter the allow user to change description for application.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class DescriptionUpdatePresenter implements DescriptionUpdateView.ActionDelegate {
    private DescriptionUpdateView          view;
    private ConsolePart                    console;
    private LoginPresenter                 loginPresenter;
    private EventBus                       eventBus;
    private AWSLocalizationConstant        constant;
    private BeanstalkClientService         service;
    private ResourceProvider               resourceProvider;
    private AsyncCallback<ApplicationInfo> callback;
    private ApplicationInfo                applicationInfo;

    /**
     * Create view.
     *
     * @param view
     * @param console
     * @param loginPresenter
     * @param eventBus
     * @param constant
     * @param service
     * @param resourceProvider
     */
    @Inject
    public DescriptionUpdatePresenter(DescriptionUpdateView view, ConsolePart console,
                                      LoginPresenter loginPresenter, EventBus eventBus,
                                      AWSLocalizationConstant constant, BeanstalkClientService service, ResourceProvider resourceProvider) {
        this.view = view;
        this.console = console;
        this.loginPresenter = loginPresenter;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.resourceProvider = resourceProvider;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog(ApplicationInfo applicationInfo, AsyncCallback<ApplicationInfo> callback) {
        this.applicationInfo = applicationInfo;
        this.callback = callback;

        if (!view.isShown()) {
            view.enableUpdateButton(false);
            view.showDialog();
            view.focusDescriptionField();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onUpdateClicked();
            }
        };

        DtoClientImpls.UpdateApplicationRequestImpl updateApplicationRequest = DtoClientImpls.UpdateApplicationRequestImpl.make();
        updateApplicationRequest.setApplicationName(applicationInfo.getName());
        updateApplicationRequest.setDescription(view.getDescriptionValue());

        DtoClientImpls.ApplicationInfoImpl dtoApplicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(dtoApplicationInfo);

        try {
            service.updateApplication(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(), updateApplicationRequest,
                                      new AwsAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              if (callback != null) {
                                                  callback.onSuccess(null);
                                              }
                                              String message = constant.updateApplicationFailed(applicationInfo.getName());
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(ApplicationInfo result) {
                                              view.close();

                                              if (callback != null) {
                                                  callback.onSuccess(result);
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onDescriptionFieldChangedValue() {
        view.enableUpdateButton(view.getDescriptionValue() != null && !view.getDescriptionValue().isEmpty());
    }
}
