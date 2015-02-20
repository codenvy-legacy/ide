/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.projecttype.wizard.presenter.ProjectWizardPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;

/** @author Artem Zatsarynnyy */
@Singleton
public class CreateModuleAction extends Action {

    private final ProjectWizardPresenter wizard;
    private final AnalyticsEventLogger   eventLogger;
    private final AppContext             appContext;
    private final SelectionAgent         selectionAgent;

    @Inject
    public CreateModuleAction(Resources resources,
                              ProjectWizardPresenter wizard,
                              AnalyticsEventLogger eventLogger,
                              AppContext appContext,
                              SelectionAgent selectionAgent) {
        super("Create Module...", "Create module from existing folder", resources.project());
        this.wizard = wizard;
        this.eventLogger = eventLogger;
        this.appContext = appContext;
        this.selectionAgent = selectionAgent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        FolderNode selectedFolder = getSelectedFolder();
        if (selectedFolder != null) {
            wizard.show(selectedFolder.getData());
        }
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(appContext.getCurrentProject() != null && getSelectedFolder() != null);
    }

    @Nullable
    private FolderNode getSelectedFolder() {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null) {
            if (selection.getFirstElement() instanceof FolderNode) {
                return (FolderNode)selection.getFirstElement();
            }
        }
        return null;
    }
}
