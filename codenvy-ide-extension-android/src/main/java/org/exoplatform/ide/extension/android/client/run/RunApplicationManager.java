/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.android.client.run;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.extension.android.client.AndroidExtension;
import org.exoplatform.ide.extension.android.client.AndroidExtensionService;
import org.exoplatform.ide.extension.android.client.event.*;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class RunApplicationManager implements RunApplicationHandler, StopApplicationHandler, ProjectOpenedHandler, ProjectClosedHandler {
    private ProjectModel currentProject;

    private Object applicationInstance;

    public RunApplicationManager() {
        IDE.addHandler(RunApplicationEvent.TYPE, this);
        IDE.addHandler(StopApplicationEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        currentProject = null;
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        currentProject = event.getProject();
    }

    @Override
    public void onRunApplication(RunApplicationEvent event) {
        if (currentProject != null && ProjectType.PHP.value().equals(currentProject.getProjectType())) {
            runApplication();
        } else {
            Dialogs.getInstance().showInfo(AndroidExtension.LOCALIZATION.notAndroidProject());
        }
    }

    @Override
    public void onStopApplication(StopApplicationEvent event) {
        if (applicationInstance != null) {
            stopApplication();
        }
    }

    //TODO change Object to application model
    private void runApplication() {
        AutoBean<Object> autoBean =
                AndroidExtension.AUTO_BEAN_FACTORY.create(Object.class);
        AutoBeanUnmarshaller<Object> unmarshaller = new AutoBeanUnmarshaller<Object>(autoBean);

        IDE.fireEvent(
                new OutputEvent(AndroidExtension.LOCALIZATION.startingProjectMessage(currentProject.getName()), OutputMessage.Type.INFO));

        try {
            AndroidExtensionService.getInstance().start(currentProject, new AsyncRequestCallback<Object>(unmarshaller) {
                @Override
                protected void onSuccess(Object result) {
                    applicationInstance = result;
                    IDE.fireEvent(new ApplicationStartedEvent(applicationInstance));

                    //TODO show to user link to application
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message =
                            (exception.getMessage() != null && !exception.getMessage().isEmpty()) ?
                            " : "
                            + exception.getMessage() : "";
                    IDE.fireEvent(new OutputEvent(
                            AndroidExtension.LOCALIZATION.startApplicationFailed()
                            + message, OutputMessage.Type.ERROR));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void stopApplication() {
        IDE.fireEvent(
                new OutputEvent(AndroidExtension.LOCALIZATION.stoppingProjectMessage(currentProject.getName()), OutputMessage.Type.INFO));

        try {
            AndroidExtensionService.getInstance().stop(currentProject, new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new ApplicationStoppedEvent(applicationInstance, true));
                    IDE.fireEvent(new OutputEvent(AndroidExtension.LOCALIZATION
                                                                  .projectStoppedMessage(currentProject.getName()),
                                                  OutputMessage.Type.INFO));
                    applicationInstance = null;
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message =
                            (exception.getMessage() != null) ? exception.getMessage()
                                                             : AndroidExtension.LOCALIZATION.stopApplicationFailed();
                    IDE.fireEvent(new OutputEvent(message, OutputMessage.Type.WARNING));

                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                            && serverException.getMessage() != null && serverException.getMessage().contains("not found")) {
                            IDE.fireEvent(new ApplicationStoppedEvent(applicationInstance, false));
                            applicationInstance = null;
                        }
                    }
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
