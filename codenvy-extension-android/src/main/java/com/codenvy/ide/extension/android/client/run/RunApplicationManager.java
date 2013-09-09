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
package com.codenvy.ide.extension.android.client.run;

import com.codenvy.ide.commons.shared.ProjectType;
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
        if (currentProject != null && (ProjectType.ANDROID.toString().equals(currentProject.getProjectType())
                                       || ProjectType.GOOGLE_MBS_ANDROID.toString().equals(currentProject.getProjectType()))) {
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
            final String apkUrl = event.getBuildStatus().getDownloadUrl();
            IDE.fireEvent(new OutputEvent(AndroidExtension.LOCALIZATION.startingProjectMessage(currentProject.getName()), OutputMessage.Type.INFO));

            if (IDE.currentWorkspace.isTemporary()){
                runApplication(AndroidExtension.LOCALIZATION.tokenForTmpWs(), apkUrl);
            } else {
                Dialogs.getInstance().askForValue("Authentication", "ManyMo Oauth token:", "", new StringValueReceivedHandler() {
                    @Override
                    public void stringValueReceived(String token) {

                        runApplication(token, apkUrl);
                    }
                }, true);
            }
        } else {
            IDE.fireEvent(new OutputEvent(AndroidExtension.LOCALIZATION.buildApplicationFailed(), OutputMessage.Type.ERROR));
        }
    }

    private void runApplication(final String token, final String apkUrl) {
        StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

        try {
            AndroidExtensionService.getInstance()
                                   .start(apkUrl, token, currentProject, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
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


}
