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
package com.codenvy.ide.extension.html.client;

import com.codenvy.ide.extension.html.client.start.ApplicationStartedEvent;
import com.codenvy.ide.extension.html.client.start.RunApplicationControl;
import com.codenvy.ide.extension.html.client.start.RunApplicationEvent;
import com.codenvy.ide.extension.html.client.start.RunApplicationHandler;
import com.codenvy.ide.extension.html.client.stop.ApplicationStoppedEvent;
import com.codenvy.ide.extension.html.client.stop.StopApplicationControl;
import com.codenvy.ide.extension.html.client.stop.StopApplicationEvent;
import com.codenvy.ide.extension.html.client.stop.StopApplicationHandler;
import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import static com.codenvy.ide.extension.html.client.HtmlRuntimeExtension.APP_RUNNER_PATH;
import static com.codenvy.ide.extension.html.client.HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS;

/**
 * Manager for running/stopping HTML applications.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunStopApplicationManager.java Jun 26, 2013 11:18:06 AM azatsarynnyy $
 */
public class RunStopApplicationManager implements RunApplicationHandler, StopApplicationHandler,
                                      VfsChangedHandler, ProjectOpenedHandler, ProjectClosedHandler {

    private ProjectModel          currentProject;

    private VirtualFileSystemInfo currentVfs;

    /** Run application. */
    private ApplicationInstance   runnedApplication;

    public RunStopApplicationManager() {
        IDE.getInstance().addControl(new StopApplicationControl());
        IDE.getInstance().addControl(new RunApplicationControl(), Docking.TOOLBAR_RIGHT);

        IDE.addHandler(RunApplicationEvent.TYPE, this);
        IDE.addHandler(StopApplicationEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    /** @see com.codenvy.ide.extension.html.client.stop.StopApplicationHandler#onStopApplication(com.codenvy.ide.extension.html.client.stop.StopApplicationEvent) */
    @Override
    public void onStopApplication(StopApplicationEvent event) {
        if (runnedApplication != null) {
            stopApplication();
        }
    }

    /** @see com.codenvy.ide.extension.html.client.start.RunApplicationHandler#onRunApplication(com.codenvy.ide.extension.html.client.start.RunApplicationEvent) */
    @Override
    public void onRunApplication(RunApplicationEvent event) {
        if (currentProject != null && ProjectType.JAVASCRIPT.value().equals(currentProject.getProjectType())) {
            runApplication();
        } else {
            Dialogs.getInstance().showInfo(HTML_LOCALIZATION_CONSTANTS.notHtmlProject());
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     *      .project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        this.currentProject = null;
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     *      .project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        this.currentProject = event.getProject();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.currentVfs = event.getVfsInfo();
    }

    /** Run HTML application. */
    private void runApplication() {
        AutoBean<ApplicationInstance> autoBean = HtmlRuntimeExtension.AUTO_BEAN_FACTORY.create(ApplicationInstance.class);
        AutoBeanUnmarshaller<ApplicationInstance> unmarshaller = new AutoBeanUnmarshaller<ApplicationInstance>(autoBean);

        try {
            HtmlRuntimeService.getInstance().start(currentVfs.getId(), currentProject.getId(),
                                                   new AsyncRequestCallback<ApplicationInstance>(unmarshaller) {
                                                       @Override
                                                       protected void onSuccess(ApplicationInstance result) {
                                                           runnedApplication = result;
                                                           IDE.fireEvent(new ApplicationStartedEvent(runnedApplication));
                                                           final String url = Window.Location.getProtocol() + "//"
                                                                              + Window.Location.getHost()
                                                                              + "/ide/" + Utils.getWorkspaceName() + "/"
                                                                              + APP_RUNNER_PATH
                                                                              + runnedApplication.getName() + "/";
                                                           final String link = "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>";
                                                           IDE.fireEvent(new OutputEvent(
                                                                                         HTML_LOCALIZATION_CONSTANTS.applicationStartedUrl(result.getName(),
                                                                                                                                           link),
                                                                                         Type.INFO));
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           String message =
                                                                            (exception.getMessage() != null && !exception.getMessage()
                                                                                                                         .isEmpty()) ?
                                                                                " : " + exception.getMessage() : "";
                                                           IDE.fireEvent(new OutputEvent(
                                                                                         HTML_LOCALIZATION_CONSTANTS.startApplicationFailed()
                                                                                             + message, OutputMessage.Type.ERROR));
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Stop HTML application. */
    private void stopApplication() {
        try {
            HtmlRuntimeService.getInstance().stop(runnedApplication.getName(), new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new ApplicationStoppedEvent(runnedApplication));
                    IDE.fireEvent(new OutputEvent(HTML_LOCALIZATION_CONSTANTS.projectStoppedMessage(currentProject.getName()),
                                                  Type.INFO));
                    runnedApplication = null;
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message = (exception.getMessage() != null) ? exception.getMessage()
                        : HTML_LOCALIZATION_CONSTANTS.stopApplicationFailed();
                    IDE.fireEvent(new OutputEvent(message, OutputMessage.Type.WARNING));

                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                            && serverException.getMessage() != null && serverException.getMessage().contains("not found")) {
                            IDE.fireEvent(new ApplicationStoppedEvent(runnedApplication));
                            runnedApplication = null;
                        }
                    }
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
