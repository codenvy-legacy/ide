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
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.RunnerPresenter;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The action for stopping application.
 * 
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class StopAction extends Action {
    private ResourceProvider resourceProvider;
    private RunnerPresenter  runner;

    @Inject
    public StopAction(ResourceProvider resourceProvider,
                      RunnerResources resources,
                      RunnerPresenter runner) {
        super("Stop Application", "Stop Application", null);
        this.resourceProvider = resourceProvider;
        this.runner = runner;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
       if (runner.isAppRunning()) {
            runner.doStopApp();
       }
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
                isEnabled = runner.isAppRunning();
            }
        }
        e.getPresentation().setEnabled(isEnabled);
    }
}