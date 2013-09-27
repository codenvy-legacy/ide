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
import com.codenvy.ide.extension.android.client.AndroidExtensionClientBundle;
import com.codenvy.ide.extension.android.client.event.RunApplicationEvent;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectHandler;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@RolesAllowed("developer")
public class RunApplicationControl extends SimpleControl implements IDEControl, ProjectClosedHandler, ProjectOpenedHandler,
                                                                    BuildProjectHandler, ProjectBuiltHandler {
    public static final String ID = "Run/Run Android Application";

    private static final String TITLE = AndroidExtension.LOCALIZATION.runApplicationControlTitle();

    private static final String PROMPT = AndroidExtension.LOCALIZATION.runApplicationControlPrompt();

    public RunApplicationControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(AndroidExtensionClientBundle.INSTANCE.runApp(), AndroidExtensionClientBundle.INSTANCE.runAppDisabled());
        setEvent(new RunApplicationEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(BuildProjectEvent.TYPE, this);
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setVisible(false);
        setEnabled(false);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        String projectType = event.getProject().getProjectType();

        boolean isAndroidProject = ProjectType.ANDROID.toString().equals(projectType) || ProjectType.GOOGLE_MBS_ANDROID.toString().equals(projectType);

        setVisible(isAndroidProject);
        setEnabled(isAndroidProject);
        setShowInContextMenu(isAndroidProject);
    }

    @Override
    public void onBuildProject(BuildProjectEvent event) {
        setEnabled(false);
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        setEnabled(true);
    }
}
