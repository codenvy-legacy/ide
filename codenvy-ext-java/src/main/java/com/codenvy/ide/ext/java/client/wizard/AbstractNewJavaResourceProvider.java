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

import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The abstract handler that provides creating of a new java file.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractNewJavaResourceProvider extends NewResourceProvider {
    public static final String TYPE_CONTENT = "\n{\n}";
    private SelectionAgent selectionAgent;


    /**
     * Create wizard's data.
     *
     * @param id
     *         resource identification
     * @param title
     *         title that will be shown on a new resource wizard
     * @param icon
     *         image that will be shown on a new resource wizard
     * @param extension
     *         extension of a resource type
     */
    public AbstractNewJavaResourceProvider(@NotNull String id,
                                           @NotNull String title,
                                           @Nullable Image icon,
                                           @Nullable String extension,
                                           @NotNull SelectionAgent selectionAgent) {
        super(id, title, icon, extension);
        this.selectionAgent = selectionAgent;
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
        return name + '.' + getExtension();
    }

    /** @return package name */
    protected String getPackage(@NotNull Folder parent, @NotNull Array<SourceFolder> sourceFolders) {
        if (parent instanceof SourceFolder) {
            return "\n";
        }
        for (SourceFolder sourceFolder : sourceFolders.asIterable()) {
            if (parent.getPath().startsWith(sourceFolder.getPath())) {
                String packageName = parent.getPath().replaceFirst(sourceFolder.getPath(), "");
                packageName = packageName.startsWith("/") ? packageName.replaceFirst("/", "").replaceAll("/", ".") : packageName.replaceAll("/", ".");
                return "package " + packageName + ";\n\n";  
            }
        }
        
        return "";
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
//        Selection<?> selection = selectionAgent.getSelection();
//        if (selection != null) {
//            if (selectionAgent.getSelection().getFirstElement() instanceof Resource) {
//                Resource resource = (Resource)selectionAgent.getSelection().getFirstElement();
//                if (resource.isFile()) {
//                    resource = resource.getParent();
//                }
//                return resource instanceof com.codenvy.ide.ext.java.client.projectmodel.Package || resource instanceof SourceFolder;
//            }
//        }
        return true;
    }
}