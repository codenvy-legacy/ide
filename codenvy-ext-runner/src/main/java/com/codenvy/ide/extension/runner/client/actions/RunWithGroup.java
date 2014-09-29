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
package com.codenvy.ide.extension.runner.client.actions;

import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 'Run With...' menu group.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunWithGroup extends DefaultActionGroup {
    private final AppContext appContext;

    @Inject
    public RunWithGroup(RunnerLocalizationConstant localizationConstants, RunnerResources resources, ActionManager actionManager,
                        AppContext appContext) {
        super(localizationConstants.runWithActionTitle(), true, actionManager);
        this.appContext = appContext;
        getTemplatePresentation().setDescription(localizationConstants.runWithActionDescription());
        getTemplatePresentation().setSVGIcon(resources.runWith());
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }
}
