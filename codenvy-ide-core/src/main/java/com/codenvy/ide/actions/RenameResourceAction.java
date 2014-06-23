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
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.rename.RenameResourcePresenter;
import com.google.inject.Inject;

/**
 * Action for changing resource's name.
 *
 * @author Ann Shumilova
 */
public class RenameResourceAction extends Action {

    private final SelectionAgent          selectionAgent;
    private final RenameResourcePresenter presenter;
    private final AnalyticsEventLogger    eventLogger;

    @Inject
    public RenameResourceAction(RenameResourcePresenter presenter, SelectionAgent selectionAgent,
                                CoreLocalizationConstant localization, AnalyticsEventLogger eventLogger, Resources resources) {
        super(localization.renameButton(), "Rename resource", null, resources.rename());

        this.selectionAgent = selectionAgent;
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: File rename");
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        final Resource resource = selection.getFirstElement();
        presenter.renameResource(resource);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        final boolean isAnySelection = selection != null && selection.getFirstElement() != null;

        // TODO: temporary disable renaming Java packages since we don't have java-refactoring feature
        if (isAnySelection && "java.package".equals(selection.getFirstElement().getResourceType())) {
            e.getPresentation().setEnabled(false);
            return;
        }

        e.getPresentation().setEnabled(isAnySelection);
    }

}
