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
package com.codenvy.ide.extension.builder.client.console.indicators;

import com.codenvy.api.builder.dto.BuilderMetric;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.codenvy.ide.extension.builder.client.build.BuildController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show time when build task finished.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildFinishedIndicator extends IndicatorAction {
    private final BuildController buildController;

    @Inject
    public BuildFinishedIndicator(BuildController buildController, BuilderResources resources) {
        super("Finished", false, 215, resources);
        this.buildController = buildController;
    }

    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        final BuilderMetric metric = buildController.getLastBuildEndTime();
        if (metric != null) {
            presentation.putClientProperty(Properties.DATA_PROPERTY, metric.getValue());
            presentation.putClientProperty(Properties.HINT_PROPERTY, metric.getDescription());
        } else {
            presentation.putClientProperty(Properties.DATA_PROPERTY, "--:--:--");
        }
    }
}
