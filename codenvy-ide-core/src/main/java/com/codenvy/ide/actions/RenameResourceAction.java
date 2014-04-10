/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.actions;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.rename.RenameResourcePresenter;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.google.inject.Inject;

/**
 * Action for changing resource's name.
 *
 * @author Ann Shumilova
 */
public class RenameResourceAction extends Action {

    private SelectionAgent          selectionAgent;
    private ResourceProvider        resourceProvider;
    private RenameResourcePresenter presenter;

    @Inject
    public RenameResourceAction(RenameResourcePresenter presenter, SelectionAgent selectionAgent,
                                ResourceProvider resourceProvider,
                                CoreLocalizationConstant localization) {
        super(localization.renameButton(), "Rename resource", null);

        this.selectionAgent = selectionAgent;
        this.resourceProvider = resourceProvider;
        this.presenter = presenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        final Resource resource = selection.getFirstElement();
        presenter.renameResource(resource);
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        if (activeProject != null && selection != null) {
            Resource resource = selection.getFirstElement();
            e.getPresentation().setEnabled(resource != null);
        } else if (activeProject == null && selection != null) {
            Resource resource = selection.getFirstElement();
            e.getPresentation().setEnabled(resource != null);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

}
