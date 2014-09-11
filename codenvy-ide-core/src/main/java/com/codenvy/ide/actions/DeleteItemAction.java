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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.part.projectexplorer.DeleteItemHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action for deleting an item which is selected in 'Project Explorer'.
 * 
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeleteItemAction extends Action {
    private AnalyticsEventLogger eventLogger;
    private SelectionAgent       selectionAgent;
    private DeleteItemHandler  deleteItemPresenter;

    @Inject
    public DeleteItemAction(Resources resources,
                            AnalyticsEventLogger eventLogger,
                            SelectionAgent selectionAgent,
                            DeleteItemHandler deleteItemPresenter, CoreLocalizationConstant localization) {
        super(localization.deleteItemActionText(), localization.deleteItemActionDescription(), null, resources.delete());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.deleteItemPresenter = deleteItemPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Delete file");

        Selection< ? > selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            deleteItemPresenter.delete((AbstractTreeNode)selection.getFirstElement());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        boolean isEnabled = false;
        Selection< ? > selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            isEnabled = ((AbstractTreeNode)selection.getFirstElement()).isDeletable();
        }
        e.getPresentation().setEnabled(isEnabled);
    }
}
