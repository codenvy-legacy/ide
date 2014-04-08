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
package com.codenvy.ide.tutorial.wizard.newresource.provider;

import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.MimeType.TEXT_XML;
import static com.codenvy.ide.api.resources.model.Folder.TYPE;

/**
 * Provides creating of a my new resource.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyResourceProvider extends NewResourceProvider {
    public static final String LOGIN_PLACE    = "@login";
    public static final String PASSWORD_PLACE = "@password";
    private SelectionAgent selectionAgent;

    @Inject
    public MyResourceProvider(SelectionAgent selectionAgent) {
        super("My file", "My file", null, "xml");
        this.selectionAgent = selectionAgent;
    }

    /** {@inheritDoc} */
    @Override
    public void create(@NotNull String name, @NotNull Folder parent, @NotNull Project project,
                       @NotNull final AsyncCallback<Resource> callback) {
        String fileName = name + '.' + getExtension();
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<configuration>\n" +
                         "    <login>" + LOGIN_PLACE + "</login>\n" +
                         "    <password>" + PASSWORD_PLACE + "</password>\n" +
                         "</configuration>";

        project.createFile(parent, fileName, content, TEXT_XML, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
        // Possible to create this type of resource just in folder
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selectionAgent.getSelection().getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selectionAgent.getSelection().getFirstElement();
                if (resource.isFile()) {
                    resource = resource.getParent();
                }
                return resource.getResourceType().equals(TYPE);
            }
        }
        return false;
    }
}