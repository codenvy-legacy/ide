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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy;

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
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.EnvironmentsInfoListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentStartedHandler;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateEnvironmentRequest;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for deploy version view.
 * The view must implement {@link DeployVersionPresenter.Display} interface and pointed in Views.gwt.xml file.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployVersionPresenter.java Sep 27, 2012 6:05:22 PM azatsarynnyy $
 */
public class DeployVersionPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
            VfsChangedHandler, DeployVersionHandler, ViewClosedHandler {
    
    interface Display extends IsView {
        /**
         * Get the 'Deploy to a new environment' mode radio field.
         *
         * @return {@link HasValue}
         */
        HasValue<Boolean> getNewEnvironmentMode();

        /**
         * Get the 'Deploy to an existing environment' mode radio field.
         *
         * @return {@link HasValue}
         */
        HasValue<Boolean> getExistingEnvironmentMode();

        /**
         * Get the environment field.
         *
         * @return {@link HasValue}
         */
        HasValue<String> getEnvironmentsField();

        /**
         * Set environments field value.
         *
         * @param values
         *         {@link LinkedHashMap} where key is represents the item's value
         *         and value represents the text of the item to be added
         */
        void setEnvironmentsValues(LinkedHashMap<String, String> values);

        /**
         * Change the enable state of the environments field.
         *
         * @param enable
         *         enabled or not
         */
        void enableEnvironmentsField(boolean value);

        /**
         * Get deploy button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getDeployButton();

        /**
         * Change the enable state of the deploy button.
         *
         * @param enable
         *         enabled or not
         */
        void enableDeployButton(boolean value);

        /**
         * Get cancel button click handler.
         *
         * @return {@link HasClickHandlers} click handler
         */
        HasClickHandlers getCancelButton();

    }

    private Display display;

    private ProjectModel openedProject;

    private VirtualFileSystemInfo vfsInfo;

    private String applicationName;

    private String versionLabel;

    private DeployVersionStartedHandler deployVersionStartedHandler;

    private Map<String, EnvironmentInfo> environments = new HashMap<String, EnvironmentInfo>();

    private LaunchEnvironmentStartedHandler launchEnvironmentStartedHandler = new LaunchEnvironmentStartedHandler() {

        @Override
        public void onLaunchEnvironmentStarted(EnvironmentInfo environmentInfo) {
            if (environmentInfo == null) {
                return;
            }
            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentLaunching(environmentInfo
                                                                                                                .getName()), Type.INFO));
            RequestStatusHandler environmentStatusHandler =
                    new EnvironmentRequestStatusHandler(
                            AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentLaunching(environmentInfo.getName()),
                            AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentSuccess(environmentInfo.getName()));
            new EnvironmentStatusChecker(vfsInfo, openedProject, environmentInfo, true, environmentStatusHandler)
                    .startChecking();
        }
    };

    public DeployVersionPresenter() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(DeployVersionEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind display (view) with presenter. */
    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDeployButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (display.getExistingEnvironmentMode().getValue()) {
                    deployVersion();
                } else if (display.getNewEnvironmentMode().getValue()) {
                    IDE.getInstance().closeView(display.asView().getId());
                    IDE.fireEvent(new LaunchEnvironmentEvent(vfsInfo.getId(), openedProject.getId(), applicationName,
                                                             versionLabel, launchEnvironmentStartedHandler));
                }
            }
        });

        display.getNewEnvironmentMode().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                display.enableEnvironmentsField(false);
                display.enableDeployButton(true);
            }
        });

        display.getExistingEnvironmentMode().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                display.enableEnvironmentsField(true);
                display.enableDeployButton(display.getEnvironmentsField().getValue() != null);
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

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionHandler#onDeployVersion(org.exoplatform.ide
     * .extension.aws.client.beanstalk.versions.deploy.DeployVersionEvent) */
    @Override
    public void onDeployVersion(DeployVersionEvent event) {
        this.applicationName = event.getApplicationName();
        this.versionLabel = event.getVersionLabel();
        this.deployVersionStartedHandler = event.getDeployVersionStartedHandler();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        getEnvironments();
    }

    /** Update pointed environment with the selected version. */
    private void deployVersion() {
        UpdateEnvironmentRequest updateEnvironmentRequest =
                AWSExtension.AUTO_BEAN_FACTORY.updateEnvironmentRequest().as();
        updateEnvironmentRequest.setVersionLabel(versionLabel);

        AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
        final String environmentId = display.getEnvironmentsField().getValue();

        try {
            BeanstalkClientService.getInstance().updateEnvironment(
                    environmentId,
                    updateEnvironmentRequest,
                    new AwsAsyncRequestCallback<EnvironmentInfo>(new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean),
                                                                 new LoggedInHandler() {

                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         deployVersion();
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void onSuccess(EnvironmentInfo result) {
                            if (display != null) {
                                IDE.getInstance().closeView(display.asView().getId());
                            }
                            if (deployVersionStartedHandler != null) {
                                deployVersionStartedHandler.onDeployVersionStarted(environments.get(environmentId));
                            }
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            String message = AWSExtension.LOCALIZATION_CONSTANT.deployVersionFailed(versionLabel);
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get the environment list. */
    private void getEnvironments() {
        try {
            BeanstalkClientService.getInstance().getEnvironments(
                    vfsInfo.getId(),
                    openedProject.getId(),
                    new AwsAsyncRequestCallback<List<EnvironmentInfo>>(new EnvironmentsInfoListUnmarshaller(),
                           new LoggedInHandler() {

                               @Override
                               public void onLoggedIn() {
                                   getEnvironments();
                               }
                           }, null) {

                        @Override
                        protected void onSuccess(List<EnvironmentInfo> result) {
                            LinkedHashMap<String, String> values = new LinkedHashMap<String, String>(result.size());
                            for (EnvironmentInfo environmentInfo : result) {
                                if (environmentInfo.getStatus() == EnvironmentStatus.Ready) {
                                    values.put(environmentInfo.getId(), environmentInfo.getName());
                                    environments.put(environmentInfo.getId(), environmentInfo);
                                }
                            }
                            display.setEnvironmentsValues(values);
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

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        this.openedProject = null;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        this.openedProject = event.getProject();
    }

}
