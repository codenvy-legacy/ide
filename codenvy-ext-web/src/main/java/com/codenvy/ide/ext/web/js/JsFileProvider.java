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
package com.codenvy.ide.ext.web.js;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.ext.web.WebExtensionResource;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;


/**
 * Provides creating of a new CSS css.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class JsFileProvider extends NewResourceProvider {

    private String content = "/** Created with Codenvy IDE */";

    @Inject
    public JsFileProvider(WebExtensionResource resources) {
        super("JavaScript file", "JavaScript file", resources.js(), "js");
    }

    /** {@inheritDoc} */
    @Override
    public void create(@NotNull String name, @NotNull Folder parent, @NotNull Project project,
                       @NotNull final AsyncCallback<Resource> callback) {
        String fileName = name + '.' + getExtension();
        project.createFile(parent, fileName, content, MimeType.TEXT_JAVASCRIPT, new AsyncCallback<File>() {
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
}