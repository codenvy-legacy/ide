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
package com.codenvy.ide.actions.rename;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;

/**
 * Action for renaming item that is selected in Project Explorer.
 *
 * @author Ann Shumilova
 */
public class RenameItemAction extends Action {

    private final SelectionAgent          selectionAgent;
    private final RenameResourcePresenter presenter;
    private final AnalyticsEventLogger    eventLogger;

    @Inject
    public RenameItemAction(RenameResourcePresenter presenter, SelectionAgent selectionAgent,
                            CoreLocalizationConstant localization, AnalyticsEventLogger eventLogger, Resources resources) {
        super(localization.renameItemActionText(), localization.renameItemActionDescription(), null, resources.rename());

        this.selectionAgent = selectionAgent;
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: File rename");

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            Object firstElement = selection.getFirstElement();
            if (firstElement instanceof ItemReference) {
                presenter.renameItem(((Selection<ItemReference>)selection).getFirstElement());
            } else if (firstElement instanceof ProjectReference) {
                presenter.renameProject(((Selection<ProjectReference>)selection).getFirstElement());
            } else if (firstElement instanceof ProjectDescriptor) {
                presenter.renameProject(((Selection<ProjectDescriptor>)selection).getFirstElement());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            Object firstElement = selection.getFirstElement();
            e.getPresentation().setEnabled(firstElement instanceof ItemReference ||
                                           firstElement instanceof ProjectReference ||
                                           firstElement instanceof ProjectDescriptor);
        }
    }
}
