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
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;

import java.util.Set;

/**
 * Action for renaming item.
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
public class RenameItemAction extends Action {
    private final SelectionAgent       selectionAgent;
    private final AnalyticsEventLogger eventLogger;
    private final Set<RenameProvider>  renameProviders;

    @Inject
    public RenameItemAction(SelectionAgent selectionAgent,
                            CoreLocalizationConstant localization,
                            AnalyticsEventLogger eventLogger,
                            Resources resources,
                            Set<RenameProvider> renameProviders) {
        super(localization.renameItemActionText(), localization.renameItemActionDescription(), null, resources.rename());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.renameProviders = renameProviders;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: File rename");

        Selection<?> selection = selectionAgent.getSelection();
        for (RenameProvider renameProvider : renameProviders) {
            if (renameProvider.canRename(selection.getFirstElement())) {
                renameProvider.renameItem(selection.getFirstElement());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        boolean isEnabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            Object firstElement = selection.getFirstElement();
            for (RenameProvider<?> renameProvider : renameProviders) {
                if (renameProvider.canRename(firstElement)) {
                    isEnabled = true;
                    break;
                }
            }
        }
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }
}
