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
package com.codenvy.ide.extension.runner.client.console.indicators;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to open a new tab sowing all log messages in a row form
 * 
 * @author Stephane Tournie
 */
@Singleton
public class LogMessagesURLIndicator extends IndicatorAction {

    private final AppContext appContext;

    @Inject
    public LogMessagesURLIndicator(RunnerResources resources, AppContext appContext) {
        super("Logs", true, 100, resources);
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null && currentProject.getProcessDescriptor() != null) {
            final Link viewLogsLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(),
                                                          Constants.LINK_REL_VIEW_LOG);
            if (viewLogsLink == null) {
                presentation.putClientProperty(Properties.DATA_PROPERTY, "");
            } else {
                presentation.putClientProperty(Properties.DATA_PROPERTY, viewLogsLink.getHref());
            }
        } else {
            presentation.putClientProperty(Properties.DATA_PROPERTY, "");
        }
    }
}
