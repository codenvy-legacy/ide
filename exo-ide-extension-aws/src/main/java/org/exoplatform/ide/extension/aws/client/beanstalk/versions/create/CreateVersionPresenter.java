/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationVersionRequest;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 12:00:58 PM anya $
 */
public class CreateVersionPresenter implements CreateVersionHandler, ViewClosedHandler, ProjectBuiltHandler {
    interface Display extends IsView {
        TextFieldItem getVersionLabelField();

        TextFieldItem getDescriptionField();

        TextFieldItem getS3BucketField();

        TextFieldItem getS3KeyField();

        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        void enableCreateButton(boolean enabled);

        void focusInVersionLabelField();
    }

    private Display display;

    private String vfsId;

    private ProjectModel project;

    private String applicationName;

    private String warUrl = null;

    private VersionCreatedHandler versionCreatedHandler;

    public CreateVersionPresenter() {
        IDE.addHandler(CreateVersionEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                warUrl = null;
                beforeCreation();
            }
        });

        display.getVersionLabelField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(event.getValue() != null);
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

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionHandler#onCreateVersion(org.exoplatform.ide
     * .extension.aws.client.beanstalk.versions.create.CreateVersionEvent) */
    @Override
    public void onCreateVersion(CreateVersionEvent event) {
        this.vfsId = event.getVfsId();
        this.project = event.getProject();
        this.applicationName = event.getApplicationName();
        this.versionCreatedHandler = event.getVersionCreatedHandler();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.enableCreateButton(false);
        display.focusInVersionLabelField();
    }

    private void beforeCreation() {
        ProjectType projectType = ProjectType.fromValue(project.getProjectType());
        if (ProjectResolver.getProjectTypesByLanguage(Language.JAVA).contains(projectType)) {
            IDE.addHandler(ProjectBuiltEvent.TYPE, this);
            JobManager.get().showJobSeparated();
            IDE.fireEvent(new BuildProjectEvent(project));
        } else {
            createVersion();
        }
    }

    private void createVersion() {
        final String versionLabel = display.getVersionLabelField().getValue();
        CreateApplicationVersionRequest createApplicationVersionRequest =
                AWSExtension.AUTO_BEAN_FACTORY.createVersionRequest().as();
        createApplicationVersionRequest.setApplicationName(applicationName);
        createApplicationVersionRequest.setDescription(display.getDescriptionField().getValue());
        createApplicationVersionRequest.setVersionLabel(versionLabel);
        createApplicationVersionRequest.setS3Bucket(display.getS3BucketField().getValue());
        createApplicationVersionRequest.setS3Key(display.getS3KeyField().getValue());
        createApplicationVersionRequest.setWar(warUrl);

        AutoBean<ApplicationVersionInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationVersionInfo();

        try {
            BeanstalkClientService.getInstance().createVersion(
                    vfsId,
                    project.getId(),
                    createApplicationVersionRequest,
                    new AwsAsyncRequestCallback<ApplicationVersionInfo>(new AutoBeanUnmarshaller<ApplicationVersionInfo>(
                            autoBean), new LoggedInHandler() {

                        @Override
                        public void onLoggedIn() {
                            createVersion();
                        }
                    }) {

                        @Override
                        protected void onSuccess(ApplicationVersionInfo result) {
                            if (display != null) {
                                IDE.getInstance().closeView(display.asView().getId());
                            }
                            if (versionCreatedHandler != null) {
                                versionCreatedHandler.onVersionCreate(result);
                            }
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            String message = AWSExtension.LOCALIZATION_CONSTANT.createVersionFailed(versionLabel);
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

    /** @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven
     * .client.event.ProjectBuiltEvent) */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createVersion();
        }
    }
}
