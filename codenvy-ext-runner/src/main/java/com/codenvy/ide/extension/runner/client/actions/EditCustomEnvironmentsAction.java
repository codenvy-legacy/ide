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

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironmentsPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to open a dialog for editing custom environments.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditCustomEnvironmentsAction extends Action {

    private final AppContext                  appContext;
    private final CustomEnvironmentsPresenter customEnvironmentsPresenter;

    @Inject
    public EditCustomEnvironmentsAction(RunnerResources resources,
                                        RunnerLocalizationConstant constants,
                                        AppContext appContext,
                                        CustomEnvironmentsPresenter customEnvironmentsPresenter) {
        super(constants.editCustomEnvironmentsActionText(), constants.editCustomEnvironmentsActionDescription(), null,
              resources.editCustomEnvironments());
        this.appContext = appContext;
        this.customEnvironmentsPresenter = customEnvironmentsPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        customEnvironmentsPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(appContext.getCurrentProject() != null);
    }
}
