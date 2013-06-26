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
package com.codenvy.ide.extension.html.client.run;

import com.codenvy.ide.extension.html.client.HtmlRuntimeExtension;
import com.codenvy.ide.extension.html.client.HtmlRuntimeService;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStartedEvent;
import com.codenvy.ide.extension.html.client.run.event.ApplicationStoppedEvent;
import com.codenvy.ide.extension.html.client.run.event.RunApplicationEvent;
import com.codenvy.ide.extension.html.client.run.event.RunApplicationHandler;
import com.codenvy.ide.extension.html.client.run.event.StopApplicationEvent;
import com.codenvy.ide.extension.html.client.run.event.StopApplicationHandler;
import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
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
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Manager for running/stopping HTML application.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunStopApplicationManager.java Jun 26, 2013 11:18:06 AM azatsarynnyy $
 */
public class RunStopApplicationManager implements RunApplicationHandler, StopApplicationHandler,
                                  VfsChangedHandler, ProjectOpenedHandler, ProjectClosedHandler {

    private ProjectModel          currentProject;

    private VirtualFileSystemInfo currentVfs;

    /** Run application. */
    private ApplicationInstance   runApplication;

    public RunStopApplicationManager() {
        IDE.getInstance().addControl(new StopApplicationControl());
        IDE.getInstance().addControl(new RunApplicationControl(), Docking.TOOLBAR_RIGHT);

        IDE.addHandler(RunApplicationEvent.TYPE, this);
        IDE.addHandler(StopApplicationEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    /** @see com.codenvy.ide.extension.html.client.run.event.StopApplicationHandler#onStopApplication(com.codenvy.ide.extension.html.client.run.event.StopApplicationEvent) */
    @Override
    public void onStopApplication(StopApplicationEvent event) {
        if (runApplication != null) {
            stopApplication();
        }
    }

    /** @see com.codenvy.ide.extension.html.client.run.event.RunApplicationHandler#onRunApplication(com.codenvy.ide.extension.html.client.run.event.RunApplicationEvent) */
    @Override
    public void onRunApplication(RunApplicationEvent event) {
        if (currentProject != null && ProjectType.JAVASCRIPT.value().equals(currentProject.getProjectType())) {
            runApplication();
        } else {
            Dialogs.getInstance().showInfo(HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.notHtmlProject());
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
        AutoBean<ApplicationInstance> autoBean =
                                                 HtmlRuntimeExtension.AUTO_BEAN_FACTORY.create(ApplicationInstance.class);
        AutoBeanUnmarshallerWS<ApplicationInstance> unmarshaller = new AutoBeanUnmarshallerWS<ApplicationInstance>(autoBean);

        try {
            IDE.fireEvent(new OutputEvent(
                                          HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.startingProjectMessage(currentProject
                                                                                                                                .getName()),
                                          Type.INFO));
            HtmlRuntimeService.getInstance().start(currentVfs.getId(), currentProject,
                                                   new RequestCallback<ApplicationInstance>(unmarshaller) {
                                                       @Override
                                                       protected void onSuccess(ApplicationInstance result) {
                                                           runApplication = result;
                                                           IDE.fireEvent(new ApplicationStartedEvent(runApplication));
                                                           String url =
                                                                        (result.getHost().startsWith("http://")) ? result.getHost()
                                                                            : "http://" +
                                                                              result.getHost();
                                                           String link = "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>";
                                                           IDE.fireEvent(new OutputEvent(
                                                                                         HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.applicationStartedUrl(
                                                                                                                                                                result.getName(),
                                                                                                                                                                link),
                                                                                         Type.INFO));
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           String message =
                                                                            (exception.getMessage() != null && !exception.getMessage()
                                                                                                                         .isEmpty()) ?
                                                                                " : "
                                                                                    + exception.getMessage() : "";
                                                           IDE.fireEvent(new OutputEvent(
                                                                                         HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.startApplicationFailed()
                                                                                             + message, OutputMessage.Type.ERROR));
                                                       }
                                                   });
        } catch (WebSocketException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Stop HTML application. */
    private void stopApplication() {
        try {
            IDE.fireEvent(new OutputEvent(
                                          HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.stoppingProjectMessage(runApplication
                                                                                                                                .getName()),
                                          Type.INFO));
            HtmlRuntimeService.getInstance().stop(runApplication.getName(), new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new ApplicationStoppedEvent(runApplication, true));
                    IDE.fireEvent(new OutputEvent(
                                                  HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS
                                                                                                  .projectStoppedMessage(currentProject.getName()),
                                                  Type.INFO));
                    runApplication = null;
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String message =
                                     (exception.getMessage() != null) ? exception.getMessage()
                                         : HtmlRuntimeExtension.HTML_LOCALIZATION_CONSTANTS.stopApplicationFailed();
                    IDE.fireEvent(new OutputEvent(message, OutputMessage.Type.WARNING));

                    if (exception instanceof ServerException) {
                        ServerException serverException = (ServerException)exception;
                        if (HTTPStatus.INTERNAL_ERROR == serverException.getHTTPStatus()
                            && serverException.getMessage() != null && serverException.getMessage().contains("not found")) {
                            IDE.fireEvent(new ApplicationStoppedEvent(runApplication, false));
                            runApplication = null;
                        }
                    }
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
