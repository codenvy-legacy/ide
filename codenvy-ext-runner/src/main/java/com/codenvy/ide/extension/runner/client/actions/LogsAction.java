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

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.ApplicationRunnerClientService;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerPresenter;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.resources.model.Project;
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
    private RunnerPresenter                runner;
    private ApplicationRunnerClientService service;
    private ConsolePart                    console;
    private EventBus                       eventBus;

    @Inject
    public LogsAction(ResourceProvider resourceProvider,
                      RunnerPresenter runner,
                      ApplicationRunnerClientService service,
                      RunnerLocalizationConstant constant,
                      RunnerResources resources,
                      ConsolePart console,
                      EventBus eventBus) {
        super(constant.showLogsControlTitle(), constant.showLogsControlPrompt(), null);
        this.resourceProvider = resourceProvider;
        this.runner = runner;
        this.service = service;
        this.console = console;
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationProcessDescriptor runningApp = getRunningApp();
        getLogs(runningApp.getUrl());
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
    private ApplicationProcessDescriptor getRunningApp() {
        ApplicationProcessDescriptor runningApp = runner.getRunningApp();
        if (runningApp == null) {
//            return runner.getRunningApp();
        }
        return runningApp;
    }

    /**
     * Return logs for application with current name.
     *
     * @param appName
     *         application name
     */
    private void getLogs(@NotNull String appName) {
//        StringUnmarshaller unmarshaller = new StringUnmarshaller();
//        try {
//            service.getLogs(appName, new AsyncRequestCallback<String>(unmarshaller) {
//                @Override
//                protected void onSuccess(String result) {
//                    console.print("<pre>" + result + "</pre>");
//                }
//
//                @Override
//                protected void onFailure(Throwable exception) {
//                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
//                    console.print(exception.getMessage());
//                }
//            });
//        } catch (RequestException e) {
//            eventBus.fireEvent(new ExceptionThrownEvent(e));
//            console.print(e.getMessage());
//        }
    }
}