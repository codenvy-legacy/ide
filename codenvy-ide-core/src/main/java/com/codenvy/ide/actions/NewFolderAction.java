/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class NewFolderAction extends Action {

    private Resources        resources;
    private ResourceProvider resourceProvider;
    private SelectionAgent   selectionAgent;

    @Inject
    public NewFolderAction(Resources resources, ResourceProvider resourceProvider, SelectionAgent selectionAgent) {
        super("Folder", "Create new folder", resources.folder());
        this.resources = resources;
        this.resourceProvider = resourceProvider;
        this.selectionAgent = selectionAgent;
    }

    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabled(resourceProvider.getActiveProject() != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO
//        NewFolderPagePresenter page = new NewFolderPagePresenter(resources, resourceProvider, selectionAgent);
//        WizardPresenter wizardDialog = new WizardPresenter(page, "Create folder", resources);
//        wizardDialog.showWizard();
    }
}
