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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerPresenter;
import com.codenvy.ide.ext.java.jdi.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.ext.java.jdi.client.run.ApplicationRunnerClientService;
import com.codenvy.ide.ext.java.jdi.client.run.RunnerPresenter;
import com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The action for showing log information.
 * 
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class LogsAction extends Action {
    private ResourceProvider               resourceProvider;
    private DebuggerPresenter              debugger;
    private RunnerPresenter                runner;
    private ApplicationRunnerClientService service;
    private ConsolePart                    console;
    private EventBus                       eventBus;

    @Inject
    public LogsAction(ResourceProvider resourceProvider,
                      JavaRuntimeResources resources,
                      JavaRuntimeLocalizationConstant constant,
                      DebuggerPresenter debugger,
                      RunnerPresenter runner,
                      ApplicationRunnerClientService service,
                      ConsolePart console,
                      EventBus eventBus) {
        super(constant.showLogsControlTitle(), constant.showLogsControlPrompt(), resources.logs());
        this.resourceProvider = resourceProvider;
        this.debugger = debugger;
        this.runner = runner;
        this.service = service;
        this.console = console;
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationInstance runningApp = getRunningApp();
        getLogs(runningApp.getName());
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
                isEnabled = getRunningApp() != null;
            }
        }
        e.getPresentation().setEnabled(isEnabled);
    }

    /** @return running application */
    @Nullable
    private ApplicationInstance getRunningApp() {
        ApplicationInstance runningApp = debugger.getRunningApp();
        if (runningApp == null) {
            return runner.getRunningApp();
        }
        return runningApp;
    }

    /**
     * Return logs for application with current name.
     * 
     * @param appName application name
     */
    private void getLogs(@NotNull String appName) {
        StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());
        try {
            service.getLogs(appName, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                @Override
                protected void onSuccess(StringBuilder result) {
                    console.print("<pre>" + result.toString() + "</pre>");
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
