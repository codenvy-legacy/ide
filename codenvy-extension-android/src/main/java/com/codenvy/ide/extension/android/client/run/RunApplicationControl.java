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
import com.codenvy.ide.extension.android.client.AndroidExtensionClientBundle;
import com.codenvy.ide.extension.android.client.event.*;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectHandler;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
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

        boolean isAndroidProject = ProjectType.ANDROID.value().equals(projectType);

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
