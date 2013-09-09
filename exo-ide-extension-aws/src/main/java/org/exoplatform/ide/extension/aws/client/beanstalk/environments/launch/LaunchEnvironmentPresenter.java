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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.ApplicationVersionListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.SolutionStackListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 3:37:53 PM anya $
 */
public class LaunchEnvironmentPresenter implements LaunchEnvironmentHandler, ViewClosedHandler {
    interface Display extends IsView {
        TextFieldItem getEnvNameField();

        TextFieldItem getEnvDescriptionField();

        HasValue<String> getSolutionStackField();

        void setSolutionStackValues(String[] values);

        HasValue<String> getVersionField();

        void setVersionValues(String[] values, String selectedValue);

        HasClickHandlers getLaunchButton();

        HasClickHandlers getCancelButton();

        void enableLaunchButton(boolean enabled);

        void focusInEnvNameField();
    }

    private Display display;

    private String applicationName;

    private String projectId;

    private String vfsId;

    private LaunchEnvironmentStartedHandler launchEnvironmentStartedHandler;

    public LaunchEnvironmentPresenter() {
        IDE.addHandler(LaunchEnvironmentEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getLaunchButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                launchEnvironment();
            }
        });

        display.getEnvNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableLaunchButton(event.getValue() != null && !event.getValue().isEmpty());
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

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentHandler#onLaunchEnvironment(org
     * .exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentEvent) */
    @Override
    public void onLaunchEnvironment(LaunchEnvironmentEvent event) {
        this.projectId = event.getProjectId();
        this.vfsId = event.getVfsId();
        this.applicationName = event.getApplicationName();
        this.launchEnvironmentStartedHandler = event.getEnvironmentCreatedHandler();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.enableLaunchButton(false);
        display.focusInEnvNameField();
        getSolutionStacks();
        getVersions(event.getVersionLabel());
    }

    private void getSolutionStacks() {
        try {
            BeanstalkClientService.getInstance().getAvailableSolutionStacks(
                    new AwsAsyncRequestCallback<List<SolutionStack>>(new SolutionStackListUnmarshaller(), new LoggedInHandler() {
                        @Override
                        public void onLoggedIn() {
                            getSolutionStacks();
                        }
                    }, null) {
                        @Override
                        protected void onSuccess(List<SolutionStack> result) {
                            List<String> values = new ArrayList<String>();
                            for (SolutionStack solutionStack : result) {
                                //For detail see https://jira.exoplatform.org/browse/IDE-1951
                                if (solutionStack.getPermittedFileTypes().contains("war"))
                                    values.add(solutionStack.getName());
                            }
                            display.setSolutionStackValues(values.toArray(new String[values.size()]));
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get application versions. */
    private void getVersions(final String selectedVersionLabel) {
        try {
            BeanstalkClientService.getInstance().getVersions(
                    vfsId,
                    projectId,
                    new AwsAsyncRequestCallback<List<ApplicationVersionInfo>>(new ApplicationVersionListUnmarshaller(),
                                                                              new LoggedInHandler() {

                                                                                  @Override
                                                                                  public void onLoggedIn() {
                                                                                      getVersions(selectedVersionLabel);
                                                                                  }
                                                                              }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }

                        @Override
                        protected void onSuccess(List<ApplicationVersionInfo> result) {
                            String[] values = new String[result.size()];
                            int i = 0;
                            for (ApplicationVersionInfo version : result) {
                                values[i] = version.getVersionLabel();
                                i++;
                            }
                            display.setVersionValues(values, selectedVersionLabel);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void launchEnvironment() {
        final String environmentName = display.getEnvNameField().getValue();
        CreateEnvironmentRequest createEnvironmentRequest =
                AWSExtension.AUTO_BEAN_FACTORY.createEnvironmentRequest().as();
        createEnvironmentRequest.setApplicationName(applicationName);
        createEnvironmentRequest.setVersionLabel(display.getVersionField().getValue());
        createEnvironmentRequest.setDescription(display.getEnvDescriptionField().getValue());
        createEnvironmentRequest.setEnvironmentName(environmentName);
        createEnvironmentRequest.setSolutionStackName(display.getSolutionStackField().getValue());

        AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
        try {
            BeanstalkClientService.getInstance().createEnvironment(
                    vfsId,
                    projectId,
                    createEnvironmentRequest,
                    new AwsAsyncRequestCallback<EnvironmentInfo>(new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean),
                                                                 new LoggedInHandler() {

                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         launchEnvironment();
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                            String message = AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentFailed(environmentName);
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }

                        @Override
                        protected void onSuccess(EnvironmentInfo result) {
                            JobManager.get().showJobSeparated();
                            if (display != null) {
                                IDE.getInstance().closeView(display.asView().getId());
                            }

                            if (launchEnvironmentStartedHandler != null) {
                                launchEnvironmentStartedHandler.onLaunchEnvironmentStarted(result);
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
