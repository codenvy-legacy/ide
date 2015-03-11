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
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.ide.part.projectexplorer.DeleteNodeHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

/**
 * Action for deleting an item which is selected in 'Project Explorer'.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeleteItemAction extends Action {
    private final AnalyticsEventLogger eventLogger;
    private       SelectionAgent       selectionAgent;
    private       DeleteNodeHandler    deleteNodeHandler;
    private       AppContext           appContext;

    @Inject
    public DeleteItemAction(Resources resources,
                            AnalyticsEventLogger eventLogger,
                            SelectionAgent selectionAgent,
                            DeleteNodeHandler deleteNodeHandler, CoreLocalizationConstant localization, AppContext appContext) {
        super(localization.deleteItemActionText(), localization.deleteItemActionDescription(), null, resources.delete());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.deleteNodeHandler = deleteNodeHandler;
        this.appContext = appContext;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && !selection.isEmpty() & selection.getHeadElement() instanceof StorableNode) {
            if (selection.isSingleSelection()) {
                deleteNodeHandler.delete((StorableNode)selection.getHeadElement());
            } else {
                deleteNodeHandler.deleteNodes((List<StorableNode>)selection.getAllElements());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        if ((appContext.getCurrentProject() == null && !appContext.getCurrentUser().isUserPermanent()) ||
            (appContext.getCurrentProject() != null && appContext.getCurrentProject().isReadOnly())) {
            e.getPresentation().setVisible(true);
            e.getPresentation().setEnabled(false);
            return;
        }

        boolean isEnabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() instanceof StorableNode) {
            isEnabled = ((StorableNode)selection.getFirstElement()).isDeletable();
        }
        e.getPresentation().setEnabled(isEnabled);
    }
}
