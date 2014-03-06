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
package com.codenvy.ide.tutorial.editor.provider;

import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.tutorial.editor.EditorTutorialResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.tutorial.editor.EditorTutorialExtension.GROOVY_MIME_TYPE;

/**
 * Provides creating of a new Groovy file.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewGroovyFileProvider extends NewResourceProvider {
    private EditorTutorialResource resource;

    @Inject
    public NewGroovyFileProvider(EditorTutorialResource resource, IconRegistry iconRegistry) {
        super("Groovy file", "Groovy file", iconRegistry.getDefaultIcon(), "groovy");
        this.resource = resource;
    }

    /** {@inheritDoc} */
    @Override
    public void create(@NotNull String name, @NotNull final Folder parent, @NotNull final Project project,
                       @NotNull final AsyncCallback<Resource> callback) {
        String content = resource.contentFile().getText();
        project.createFile(parent, name + '.' + getExtension(), content, GROOVY_MIME_TYPE, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File file) {
                callback.onSuccess(file);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }
}