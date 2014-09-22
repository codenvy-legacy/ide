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
 * 'Custom Images' menu group.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomImagesGroup extends DefaultActionGroup {
    private final AppContext appContext;

    @Inject
    public CustomImagesGroup(RunnerLocalizationConstant localizationConstants, RunnerResources resources, ActionManager actionManager,
                             AppContext appContext) {
        super(localizationConstants.customImagesActionTitle(), true, actionManager);
        this.appContext = appContext;
        getTemplatePresentation().setSVGIcon(resources.customImages());
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }
}
