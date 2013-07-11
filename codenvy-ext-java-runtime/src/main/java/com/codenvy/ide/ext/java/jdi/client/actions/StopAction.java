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
package com.codenvy.ide.ext.java.jdi.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.run.RunnerPresenter;
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
    private ResourceProvider  resourceProvider;
    private DebuggerPresenter debugger;
    private RunnerPresenter   runner;

    @Inject
    public StopAction(ResourceProvider resourceProvider, JavaRuntimeResources resources, DebuggerPresenter debugger, RunnerPresenter runner) {
        super("Stop Application", "Stop Application", resources.stopApp());
        this.resourceProvider = resourceProvider;
        this.debugger = debugger;
        this.runner = runner;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (debugger.isAppRunning()) {
            debugger.doStopApp();
        } else if (runner.isAppRunning()) {
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
                isEnabled = debugger.isAppRunning() || runner.isAppRunning();
            }
        }
        e.getPresentation().setEnabled(isEnabled);
    }
}
