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
package com.codenvy.ide.ext.java.jdi.client.actions;

import com.codenvy.api.builder.BuildStatus;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.extension.builder.client.build.BuildProjectPresenter;
import com.codenvy.ide.extension.builder.client.build.ProjectBuiltCallback;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to debug current project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DebugAction extends Action {

    private BuildProjectPresenter buildProjectPresenter;
    private DebuggerPresenter     debuggerPresenter;
    private ResourceProvider      resourceProvider;

    @Inject
    public DebugAction(BuildProjectPresenter buildProjectPresenter, DebuggerPresenter debuggerPresenter, JavaRuntimeResources resources,
                       ResourceProvider resourceProvider) {
        super("Debug Application", "Debug Application", resources.debugApp());
        this.buildProjectPresenter = buildProjectPresenter;
        this.debuggerPresenter = debuggerPresenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        buildProjectPresenter.buildActiveProject(new ProjectBuiltCallback() {
            @Override
            public void onBuilt(BuildStatus status) {
                if (status == BuildStatus.SUCCESSFUL) {
                    // TODO
                    debuggerPresenter.connectDebugger("127.0.0.1", 8008);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        boolean isEnabled = false;
        if (activeProject != null) {
            if (activeProject.getDescription().getNatures().contains("CodenvyExtension")) {
                e.getPresentation().setVisible(false);
            } else {
                isEnabled = true;
            }
        }
        e.getPresentation().setEnabled(isEnabled);
    }
}
