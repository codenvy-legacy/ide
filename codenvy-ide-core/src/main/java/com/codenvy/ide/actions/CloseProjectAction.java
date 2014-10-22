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
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.api.runner.dto.RunnerMetric.ALWAYS_ON;
import static com.codenvy.api.runner.dto.RunnerMetric.TERMINATION_TIME;

/** @author Andrey Plotnikov */
@Singleton
public class CloseProjectAction extends Action {

    private final AppContext               appContext;
    private       RunnerServiceClient      runnerServiceClient;
    private       CoreLocalizationConstant constant;
    private final AnalyticsEventLogger     eventLogger;
    private final EventBus                 eventBus;
    private final DialogFactory            dialogFactory;

    @Inject
    public CloseProjectAction(AppContext appContext,
                              Resources resources,
                              RunnerServiceClient runnerServiceClient,
                              CoreLocalizationConstant constant,
                              AnalyticsEventLogger eventLogger,
                              EventBus eventBus,
                              DialogFactory dialogFactory) {
        super("Close Project", "Close project", null, resources.closeProject());
        this.appContext = appContext;
        this.runnerServiceClient = runnerServiceClient;
        this.constant = constant;
        this.eventLogger = eventLogger;
        this.eventBus = eventBus;
        this.dialogFactory = dialogFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        if (appContext.getCurrentProject() != null) {
            final ApplicationProcessDescriptor processDescriptor = appContext.getCurrentProject().getProcessDescriptor();
            if (processDescriptor != null) {
                RunnerMetric runnerMetric = null;
                for (RunnerMetric runnerStat : processDescriptor.getRunStats()) {
                    if (TERMINATION_TIME.equals(runnerStat.getName())) {
                        runnerMetric = runnerStat;
                    }
                }
                if (runnerMetric != null && ALWAYS_ON.equals(runnerMetric.getValue()))
                    eventBus.fireEvent(new CloseCurrentProjectEvent());
                else {
                    final String projectName = appContext.getCurrentProject().getProjectDescription().getName();
                    dialogFactory.createConfirmDialog(constant.closeProjectAskTitle(),
                                                      constant.appWillBeStopped(projectName),
                                                      confirmStoppingAppCallback, null).show();
                }
            } else {
                eventBus.fireEvent(new CloseCurrentProjectEvent());
            }
        }
    }

    private ConfirmCallback confirmStoppingAppCallback = new ConfirmCallback() {
        @Override
        public void accepted() {
            final Link stopLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), Constants.LINK_REL_STOP);
            if (stopLink != null) {
                runnerServiceClient.stop(stopLink, new AsyncRequestCallback<ApplicationProcessDescriptor>() {
                    @Override
                    protected void onSuccess(ApplicationProcessDescriptor applicationProcessDescriptor) {
                        eventBus.fireEvent(new CloseCurrentProjectEvent());
                    }

                    @Override
                    protected void onFailure(Throwable ignore) {
                    }
                });
            }
        }
    };
}
