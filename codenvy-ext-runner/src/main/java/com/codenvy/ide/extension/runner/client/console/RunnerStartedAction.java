/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show time when runner started.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerStartedAction extends InfoAction {
    private final RunnerController runnerController;

    @Inject
    public RunnerStartedAction(RunnerController runnerController, RunnerResources resources) {
        super("Runner Started At", false, resources);
        this.runnerController = runnerController;
    }

    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.putClientProperty(Properties.DATA_PROPERTY, runnerController.getStartTime());
    }
}
