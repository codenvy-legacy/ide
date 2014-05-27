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
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * Action to create new Java file (e.g. class, enum, ...).
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewJavaResourcePresenter implements NewJavaResourceView.ActionDelegate {
    private static final String DEFAULT_CONTENT = "\n{\n}";
    private NewJavaResourceView view;
    private EditorAgent         editorAgent;
    private SelectionAgent      selectionAgent;
    private ResourceProvider    resourceProvider;
    private Array<ResourceTypes> types = Collections.createArray();

    @Inject
    public NewJavaResourcePresenter(NewJavaResourceView view,
                                    EditorAgent editorAgent,
                                    SelectionAgent selectionAgent,
                                    ResourceProvider resourceProvider) {
        this.view = view;
        this.editorAgent = editorAgent;
        this.selectionAgent = selectionAgent;
        this.resourceProvider = resourceProvider;
        this.view.setDelegate(this);
        types.add(ResourceTypes.CLASS);
        types.add(ResourceTypes.INTERFACE);
        types.add(ResourceTypes.ENUM);
    }

    @Override
    public void onOkClicked() {
        view.close();

        final String resourceName = view.getName();
        switch (view.getSelectedType()) {
            case CLASS:
                createClass(resourceName);
                break;
            case INTERFACE:
                createInterface(resourceName);
                break;
            case ENUM:
                createEnum(resourceName);
                break;
        }
    }

    private void createClass(String resourceName) {
        JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
        StringBuilder content = new StringBuilder(getPackage(getParent(), activeProject.getSourceFolders()));
        content.append("public class ").append(resourceName).append(DEFAULT_CONTENT);
        createFile(resourceName, getParent(), activeProject, content.toString());
    }

    private void createInterface(String resourceName) {
        JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
        StringBuilder content = new StringBuilder(getPackage(getParent(), (activeProject).getSourceFolders()));
        content.append("public interface ").append(resourceName).append(DEFAULT_CONTENT);
        createFile(resourceName, getParent(), activeProject, content.toString());
    }

    private void createEnum(String resourceName) {
        JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();
        StringBuilder content = new StringBuilder(getPackage(getParent(), (activeProject).getSourceFolders()));
        content.append("public enum ").append(resourceName).append(DEFAULT_CONTENT);
        createFile(resourceName, getParent(), activeProject, content.toString());
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    public void showDialog() {
        view.setTypes(types);
        view.showDialog();
    }

    private void createFile(@NotNull String name, @NotNull Folder parent, @NotNull Project project, @NotNull String content) {
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

    private String createResourceName(@NotNull String name) {
        return name + ".java";
    }

    private String getPackage(@NotNull Folder parent, @NotNull Array<SourceFolder> sourceFolders) {
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

    /** Returns parent folder for creating new resource. */
    private Folder getParent() {
        Project activeProject = resourceProvider.getActiveProject();
        Folder parent = null;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selection.getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selection.getFirstElement();
                if (resource.isFile()) {
                    parent = resource.getParent();
                } else {
                    parent = (Folder)resource;
                }
            }
        } else {
            parent = activeProject;
        }
        return parent;
    }
}
