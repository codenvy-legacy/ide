/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.newresource;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.newresource.DefaultNewResourceAction;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.resources.model.Folder.TYPE;

/**
 * Action to create new folder.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewFolderAction extends DefaultNewResourceAction {
    private ResourceProvider         resourceProvider;
    private CoreLocalizationConstant localizationConstant;
    private SelectionAgent           selectionAgent;

    @Inject
    public NewFolderAction(ResourceProvider resourceProvider,
                           CoreLocalizationConstant localizationConstant,
                           SelectionAgent selectionAgent,
                           Resources resources) {
        super(localizationConstant.actionNewFolderTitle(),
              localizationConstant.actionNewFolderDescription(),
              null,
              resources.defaultFolder(),
              resourceProvider,
              selectionAgent,
              null);
        this.resourceProvider = resourceProvider;
        this.localizationConstant = localizationConstant;
        this.selectionAgent = selectionAgent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AskValueDialog(localizationConstant.newResourceTitle(localizationConstant.actionNewFolderTitle()),
                localizationConstant.newResourceLabel(), new AskValueCallback() {
            @Override
            public void onOk(String value) {
                Project activeProject = resourceProvider.getActiveProject();
                activeProject.createFolder(getParent(), value, new AsyncCallback<Folder>() {
                    @Override
                    public void onSuccess(Folder result) {
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                });
            }
        }
        ).show();
    }

    @Override
    public void update(ActionEvent e) {
        boolean enabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (resourceProvider.getActiveProject() != null && selection != null) {
            if (selection.getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selection.getFirstElement();
                if (resource.isFile()) {
                    resource = resource.getParent();
                }
                enabled = resource instanceof Project || resource instanceof Folder && resource.getResourceType().equals(TYPE);
            }
        }
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
