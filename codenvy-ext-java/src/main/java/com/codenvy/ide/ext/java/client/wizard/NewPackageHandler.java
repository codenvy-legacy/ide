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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.ui.wizard.newresource.CreateResourceHandler;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.Package;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Provides creating of a java package.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewPackageHandler implements CreateResourceHandler {

    @Inject
    public NewPackageHandler() {
    }

    /** {@inheritDoc} */
    @Override
    public void create(@NotNull String name, @NotNull Folder parent, @NotNull Project project,
                       @NotNull final AsyncCallback<Resource> callback) {
        SourceFolder parentSourceFolder;
        String parentName;
        if (parent instanceof SourceFolder) {
            parentSourceFolder = (SourceFolder)parent;
            parentName = "";
        } else {
            parentSourceFolder = (SourceFolder)parent.getParent();
            parentName = parent.getName() + '.';
        }

        ((JavaProject)project).createPackage(parentSourceFolder, parentName + name, new AsyncCallback<Package>() {
            @Override
            public void onSuccess(Package result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
}