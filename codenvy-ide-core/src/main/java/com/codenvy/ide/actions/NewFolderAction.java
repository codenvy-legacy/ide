/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.actions;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.newfolder.NewFolderPagePresenter;
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
        NewFolderPagePresenter page = new NewFolderPagePresenter(resources, resourceProvider, selectionAgent);
        WizardPresenter wizardDialog = new WizardPresenter(page, "Create folder", resources);
        wizardDialog.showWizard();
    }
}
