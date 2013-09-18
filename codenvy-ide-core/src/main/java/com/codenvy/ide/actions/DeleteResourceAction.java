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
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class DeleteResourceAction extends Action {
    private SelectionAgent   selectionAgent;
    private ResourceProvider resourceProvider;

    @Inject
    public DeleteResourceAction(SelectionAgent selectionAgent, ResourceProvider resourceProvider, Resources resources) {
        super("Delete", "Delete resource", resources.delete());

        this.selectionAgent = selectionAgent;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        if (activeProject != null && selection != null) {
            Resource resource = selection.getFirstElement();
            e.getPresentation().setEnabled(resource != null);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        Resource resource = selection.getFirstElement();

        resourceProvider.delete(resource, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(DeleteResourceAction.class, "Exception during project removing", caught);
            }
        });
    }
}