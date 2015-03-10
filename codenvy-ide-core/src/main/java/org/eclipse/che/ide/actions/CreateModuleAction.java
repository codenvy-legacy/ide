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
package org.eclipse.che.ide.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;

import org.eclipse.che.ide.projecttype.wizard.presenter.ProjectWizardPresenter;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.tree.generic.FolderNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;

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
