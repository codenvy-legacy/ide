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
package com.codenvy.ide.newresource;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new folder.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewFolderAction extends DefaultNewResourceAction {
    private CoreLocalizationConstant localizationConstant;

    @Inject
    public NewFolderAction(AppContext appContext,
                           CoreLocalizationConstant localizationConstant,
                           SelectionAgent selectionAgent,
                           Resources resources,
                           ProjectServiceClient projectServiceClient) {
        super(localizationConstant.actionNewFolderTitle(),
              localizationConstant.actionNewFolderDescription(),
              null,
              resources.defaultFolder(),
              appContext,
              selectionAgent,
              null,
              projectServiceClient);
        this.localizationConstant = localizationConstant;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AskValueDialog(localizationConstant.newResourceTitle(localizationConstant.actionNewFolderTitle()),
                           localizationConstant.newResourceLabel(), new AskValueCallback() {
            @Override
            public void onOk(String value) {
                projectServiceClient.createFolder(getParentPath() + '/' + value, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(NewFolderAction.class, exception);
                    }
                });
            }
        }
        ).show();
    }

    @Override
    public void update(ActionEvent e) {
        boolean enabled = false;
        if (appContext.getCurrentProject() != null) {
            Selection<?> selection = selectionAgent.getSelection();
            if (selection != null) {
                enabled = selection.getFirstElement() != null;
            }
        }
        e.getPresentation().setEnabled(enabled);
    }
}
