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
package com.codenvy.ide.ext.java.client.newresource;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.codenvy.ide.newresource.DefaultNewResourceAction;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Action to create new Java file (e.g. class, enum, ...).
 *
 * @author Artem Zatsarynnyy
 */
public class NewJavaResourceAction extends DefaultNewResourceAction {
    public static final String DEFAULT_CONTENT = "\n{\n}";

    public NewJavaResourceAction(String title, String description,
                                 @Nullable ImageResource icon,
                                 @Nullable SVGResource svgIcon,
                                 ResourceProvider resourceProvider,
                                 SelectionAgent selectionAgent,
                                 EditorAgent editorAgent) {
        super(title, description, icon, svgIcon, resourceProvider, selectionAgent, editorAgent);
    }

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
    protected void createFile(@NotNull String name, @NotNull Folder parent, @NotNull Project project, @NotNull String content) {
        ((JavaProject)project).createCompilationUnit(parent, createResourceName(name), content, new AsyncCallback<CompilationUnit>() {
            @Override
            public void onSuccess(CompilationUnit result) {
                if (result.isFile()) {
                    editorAgent.openEditor(result);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    /** @return file name with extension(".java") */
    protected String createResourceName(@NotNull String name) {
        return name + ".java";
    }

    /** @return package name */
    protected String getPackage(@NotNull Folder parent, @NotNull Array<SourceFolder> sourceFolders) {
        if (parent instanceof SourceFolder) {
            return "\n";
        }
        for (SourceFolder sourceFolder : sourceFolders.asIterable()) {
            if (parent.getPath().startsWith(sourceFolder.getPath())) {
                String packageName = parent.getPath().replaceFirst(sourceFolder.getPath(), "");
                packageName = packageName.startsWith("/") ? packageName.replaceFirst("/", "").replaceAll("/", ".")
                                                          : packageName.replaceAll("/", ".");
                return "package " + packageName + ";\n\n";
            }
        }

        return "";
    }

    @Override
    public void update(ActionEvent e) {
        boolean enabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selection.getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selection.getFirstElement();
                if (resource.isFile()) {
                    resource = resource.getParent();
                }
                enabled = resource instanceof com.codenvy.ide.ext.java.client.projectmodel.Package || resource instanceof SourceFolder;
            }
        }
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
