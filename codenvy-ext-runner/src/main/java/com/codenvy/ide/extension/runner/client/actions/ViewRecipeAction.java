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

import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.RunnerController;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to view the runner 'recipe' file being used for running app.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ViewRecipeAction extends Action {
    private final RunnerController controller;
    private AppContext appContext;

    @Inject
    public ViewRecipeAction(RunnerController controller,
                            RunnerResources resources,
                            RunnerLocalizationConstant localizationConstants,
                            AppContext appContext) {
        super(localizationConstants.viewRecipeText(), localizationConstants.viewRecipeDescription(), null, resources.viewRecipe());
        this.controller = controller;
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        controller.showRecipe();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
        e.getPresentation().setEnabled(controller.isRecipeLinkExists());
    }
}
