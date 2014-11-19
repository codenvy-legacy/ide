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
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.google.inject.Inject;

/**
 * Action for coping selected item(s) in Project Explorer tree.
 *
 * @author Ann Shumilova
 */
public class MoveItemAction extends Action {
    private final AnalyticsEventLogger eventLogger;
    private SelectionAgent selectionAgent;


    @Inject
    public MoveItemAction(Resources resources,
                          AnalyticsEventLogger eventLogger,
                          SelectionAgent selectionAgent,
                          CoreLocalizationConstant localization) {
        super(localization.actionMoveText(), localization.actionMoveDescription(), null, resources.move());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof StorableNode) {

        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Selection<?> selection = selectionAgent.getSelection();
        boolean isEnabled = selection != null && !selection.isEmpty();
        e.getPresentation().setEnabled(isEnabled);
    }
}