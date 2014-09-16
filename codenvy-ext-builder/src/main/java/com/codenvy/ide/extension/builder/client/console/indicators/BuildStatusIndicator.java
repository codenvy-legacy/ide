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

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.codenvy.ide.extension.builder.client.build.BuildController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action used to show build task status.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuildStatusIndicator extends IndicatorAction {
    private final BuildController buildController;

    @Inject
    public BuildStatusIndicator(BuildController buildController, BuilderResources resources) {
        super("Status", false, 160, resources);
        this.buildController = buildController;
    }

    @Override
    public void update(ActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.putClientProperty(Properties.DATA_PROPERTY, buildController.getLastBuildStatus());
    }
}
