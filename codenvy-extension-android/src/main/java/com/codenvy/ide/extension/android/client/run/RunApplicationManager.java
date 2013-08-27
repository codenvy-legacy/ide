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
package com.codenvy.ide.extension.android.client.run;

import com.codenvy.ide.extension.android.client.AndroidExtension;
import com.codenvy.ide.extension.android.client.AndroidExtensionService;
import com.codenvy.ide.extension.android.client.event.RunApplicationEvent;
import com.codenvy.ide.extension.android.client.event.RunApplicationHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class RunApplicationManager implements RunApplicationHandler, ProjectOpenedHandler, ProjectClosedHandler, ProjectBuiltHandler {
    private ProjectModel currentProject;

    private static RunApplicationManager instance;

    public RunApplicationManager() {
        IDE.addHandler(RunApplicationEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);

        instance = this;
    }

    public static RunApplicationManager getInstance() {
        return instance;
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
        if (currentProject != null && ProjectType.ANDROID.value().equals(currentProject.getProjectType())) {
            IDE.addHandler(ProjectBuiltEvent.TYPE, this);
            IDE.fireEvent(new BuildProjectEvent(currentProject, false, true));
        } else {
            Dialogs.getInstance().showInfo(AndroidExtension.LOCALIZATION.notAndroidProject());
        }
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(ProjectBuiltEvent.TYPE, this);
        if (BuildStatus.Status.SUCCESSFUL == event.getBuildStatus().getStatus()) {
            runApplication(event.getBuildStatus().getDownloadUrl());
        } else {
            IDE.fireEvent(new OutputEvent(AndroidExtension.LOCALIZATION.buildApplicationFailed(), OutputMessage.Type.ERROR));
        }
    }

    private void runApplication(final String apkUrl) {
        IDE.fireEvent(
                new OutputEvent(AndroidExtension.LOCALIZATION.startingProjectMessage(currentProject.getName()), OutputMessage.Type.INFO));

        Dialogs.getInstance().askForValue("Authentication","ManyMo Oauth token:","", new StringValueReceivedHandler() {
            @Override
            public void stringValueReceived(String value) {
                StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

                try {
                    AndroidExtensionService.getInstance().start(apkUrl, value, currentProject, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                        @Override
                        protected void onSuccess(StringBuilder result) {
                            JSONObject response = JSONParser.parseStrict(result.toString()).isObject();
                            String applicationUrl = response.get("applicationUrl").isString().stringValue();

                            IDE.fireEvent(new OutputEvent(
                                    AndroidExtension.LOCALIZATION.applicationStartedUrl(
                                            currentProject.getName(), applicationUrl), OutputMessage.Type.INFO));
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
        }, true);

    }
}
