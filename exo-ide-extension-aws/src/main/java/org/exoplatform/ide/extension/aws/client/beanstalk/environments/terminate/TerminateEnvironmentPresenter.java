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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
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
 * @version $Id: TerminateEnvironmentPresenter.java Sep 28, 2012 5:00:35 PM azatsarynnyy $
 */
public class TerminateEnvironmentPresenter implements TerminateEnvironmentHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasClickHandlers getTerminateButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getTerminateQuestion();
    }

    private Display display;

    private EnvironmentInfo environment;

    private TerminateEnvironmentStartedHandler terminateEnvironmentStartedHandler;

    public TerminateEnvironmentPresenter() {
        IDE.addHandler(TerminateEnvironmentEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getTerminateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (environment.getStatus().equals(EnvironmentStatus.Ready))
                    terminateEnvironment();
                else
                    Dialogs.getInstance().showError("Environment alredy terminated");
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

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate.TerminateEnvironmentHandler#onTerminateEnvironment
     * (org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate.TerminateEnvironmentEvent) */
    @Override
    public void onTerminateEnvironment(TerminateEnvironmentEvent event) {
        this.environment = event.getEnvironmentInfo();
        this.terminateEnvironmentStartedHandler = event.getTerminateEnvironmentStartedHandler();
        if (!environment.getStatus().equals(EnvironmentStatus.Ready)) {
            Dialogs.getInstance().showError("Environment is in an invalid state for this operation. Must be Ready");
            return;
        }

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.getTerminateQuestion().setValue(
                AWSExtension.LOCALIZATION_CONSTANT.terminateEnvironmentQuestion(environment.getName()));
    }

    private void terminateEnvironment() {
        try {
            AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
            AutoBeanUnmarshaller<EnvironmentInfo> unmarshaller = new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean);
            BeanstalkClientService.getInstance().stopEnvironment(environment.getId(),
                 new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller,
                                                              new LoggedInHandler() {

                                                                  @Override
                                                                  public void onLoggedIn() {
                                                                      terminateEnvironment();
                                                                  }
                                                              }, null) {

                     @Override
                     protected void processFail(Throwable exception) {
                         String message = AWSExtension.LOCALIZATION_CONSTANT
                                                      .terminateEnvironmentFailed(
                                                              environment.getId());
                         if (exception instanceof ServerException &&
                             ((ServerException)exception).getMessage() != null) {
                             message += "<br>" + ((ServerException)exception).getMessage();
                         }
                         Dialogs.getInstance().showError(message);
                     }

                     @Override
                     protected void onSuccess(EnvironmentInfo result) {
                         IDE.getInstance().closeView(display.asView().getId());
                         if (terminateEnvironmentStartedHandler != null) {
                             terminateEnvironmentStartedHandler
                                     .onTerminateEnvironmentStarted(environment);
                         }
                     }
                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
