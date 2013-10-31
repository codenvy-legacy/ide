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
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The abstract handler that provides creating of a new java file.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractNewJavaFileHandler implements CreateResourceHandler {
    public static final String TYPE_CONTENT = "\n{\n}";

    /**
     * Create a java file.
     *
     * @param name
     *         java file name
     * @param parent
     *         folder where a java file needs to be created
     * @param project
     *         project where a java file needs to be created
     * @param content
     *         content of file
     */
    protected void createFile(@NotNull String name, @NotNull Folder parent, @NotNull Project project,
                              @NotNull final AsyncCallback<Resource> callback, @NotNull String content) {
        ((JavaProject)project)
                .createCompilationUnit(parent, createResourceName(name), content, new AsyncCallback<CompilationUnit>() {
                    @Override
                    public void onSuccess(CompilationUnit result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }
                });
    }

    /** @return file name with extension(".java") */
    protected String createResourceName(@NotNull String name) {
        return name + ".java";
    }

    /** @return package name */
    protected String getPackage(@NotNull Folder parent) {
        if (parent instanceof SourceFolder) {
            return "\n";
        }

        // TODO full package ?
        String packageName = parent.getName();
        return "package " + packageName + ";\n\n";
    }
}