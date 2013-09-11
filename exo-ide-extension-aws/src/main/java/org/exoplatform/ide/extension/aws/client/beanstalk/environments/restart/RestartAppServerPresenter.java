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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RestartAppServerPresenter.java Sep 28, 2012 3:56:46 PM azatsarynnyy $
 */
public class RestartAppServerPresenter implements RestartAppServerHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasClickHandlers getRestartButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getRestartQuestion();
    }

    private Display display;

    private EnvironmentInfo environment;

    public RestartAppServerPresenter() {
        IDE.addHandler(RestartAppServerEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getRestartButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                restartAppServer();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart.RestartAppServerHandler#onRestartAppServer(org
     * .exoplatform.ide.extension.aws.client.beanstalk.environments.restart.RestartAppServerEvent) */
    @Override
    public void onRestartAppServer(RestartAppServerEvent event) {
        this.environment = event.getEnvironmentInfo();
        if (!environment.getStatus().equals(EnvironmentStatus.Ready)) {
            Dialogs.getInstance().showError("Environment is in an invalid state for this operation. Must be Ready");
            return;
        }

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.getRestartQuestion().setValue(
                AWSExtension.LOCALIZATION_CONSTANT.restartAppServerQuestion(environment.getName()));
    }

    /** Restart an application server associated with the specified environment. */
    private void restartAppServer() {
        try {
            BeanstalkClientService.getInstance().restartApplicationServer(environment.getId(),
                  new AwsAsyncRequestCallback<Object>(new LoggedInHandler() {

                      @Override
                      public void onLoggedIn() {
                          restartAppServer();
                      }
                  }, null) {

                      @Override
                      protected void processFail(Throwable exception) {
                          String message = AWSExtension.LOCALIZATION_CONSTANT
                                                       .restartAppServerFailed(
                                                               environment.getId());
                          if (exception instanceof ServerException &&
                              ((ServerException)exception).getMessage() != null) {
                              message += "<br>" +
                                         ((ServerException)exception).getMessage();
                          }
                          Dialogs.getInstance().showError(message);
                      }

                      @Override
                      protected void onSuccess(Object result) {
                          IDE.getInstance().closeView(display.asView().getId());
                      }
                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
