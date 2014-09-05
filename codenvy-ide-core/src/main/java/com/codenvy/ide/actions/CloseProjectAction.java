/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.CloseCurrentProjectEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/** @author Andrey Plotnikov */
@Singleton
public class CloseProjectAction extends Action {

    private final AppContext               appContext;
    private       RunnerServiceClient      runnerServiceClient;
    private       CoreLocalizationConstant constant;
    private final AnalyticsEventLogger     eventLogger;
    private final EventBus                 eventBus;

    @Inject
    public CloseProjectAction(AppContext appContext,
                              Resources resources,
                              RunnerServiceClient runnerServiceClient,
                              CoreLocalizationConstant constant,
                              AnalyticsEventLogger eventLogger,
                              EventBus eventBus) {
        super("Close Project", "Close project", null, resources.closeProject());
        this.appContext = appContext;
        this.runnerServiceClient = runnerServiceClient;
        this.constant = constant;
        this.eventLogger = eventLogger;
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Close project");

        if (appContext.getCurrentProject() != null) {
            final ApplicationProcessDescriptor processDescriptor = appContext.getCurrentProject().getProcessDescriptor();
            if (processDescriptor != null) {
                RunnerMetric runnerMetric = null;
                for (RunnerMetric runnerStat : processDescriptor.getRunStats()) {
                    if (RunnerMetric.TERMINATION_TIME.equals(runnerStat.getName())) {
                        runnerMetric = runnerStat;
                    }
                }
                if (RunnerMetric.ALWAYS_ON.equals(runnerMetric.getValue()))
                    eventBus.fireEvent(new CloseCurrentProjectEvent());
                else {
                    String projectName = appContext.getCurrentProject().getProjectDescription().getName();
                    Ask ask = new Ask(constant.closeProjectAskTitle(), constant.appWillBeStopped(projectName), new AskHandler() {
                        @Override
                        public void onOk() {
                            Link link = RunnerUtils.getLink(processDescriptor, Constants.LINK_REL_STOP);
                            if (link != null) {
                                runnerServiceClient.stop(link, new AsyncRequestCallback<ApplicationProcessDescriptor>() {
                                    @Override
                                    protected void onSuccess(ApplicationProcessDescriptor applicationProcessDescriptor) {
                                        eventBus.fireEvent(new CloseCurrentProjectEvent());
                                    }

                                    @Override
                                    protected void onFailure(Throwable throwable) {

                                    }
                                });
                            }
                        }
                    });
                    ask.show();
                }
            } else {
                eventBus.fireEvent(new CloseCurrentProjectEvent());
            }
        }
    }
}
