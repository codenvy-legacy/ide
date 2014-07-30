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
package com.codenvy.ide.actions.delete;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;

/**
 * Action for deleting item.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeleteItemAction extends Action {
    private final SelectionAgent       selectionAgent;
    private final AnalyticsEventLogger eventLogger;
    private final Set<DeleteProvider>  deleteProviders;

    @Inject
    public DeleteItemAction(SelectionAgent selectionAgent,
                            Resources resources,
                            CoreLocalizationConstant localization,
                            AnalyticsEventLogger eventLogger,
                            Set<DeleteProvider> deleteProviders) {
        super(localization.deleteItemActionText(), localization.deleteItemActionDescription(), null, resources.delete());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.deleteProviders = deleteProviders;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        boolean isEnabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            Object firstElement = selection.getFirstElement();
            for (DeleteProvider<?> deleteProvider : deleteProviders) {
                if (deleteProvider.canDelete(firstElement)) {
                    isEnabled = true;
                    break;
                }
            }
        }
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Delete file");

        Selection<?> selection = selectionAgent.getSelection();
        for (DeleteProvider deleteProvider : deleteProviders) {
            if (deleteProvider.canDelete(selection.getFirstElement())) {
                deleteProvider.deleteItem(selection.getFirstElement());
            }
        }
    }
}
