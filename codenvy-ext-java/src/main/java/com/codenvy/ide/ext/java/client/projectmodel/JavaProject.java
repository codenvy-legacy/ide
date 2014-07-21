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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.TreeElement;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.codenvy.ide.ext.java.client.projectmodel.JavaUtils.checkCompilationUnitName;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaUtils.checkPackageName;

/**
 * A Java project represents a view of a project resource in terms of Java
 * elements such as package, compilation units.
 * A project may contain several source folders, which contain packages.
 * JavaProject overrides <code>createFolder</code> and <code>createFile</code>,
 * implementations try to create package or compilation unit if it's possible,
 * else fall back to super implementations of this methods.
 *
 * @author Nikolay Zamosenchuk
 */
public class JavaProject extends Project {
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    /** Java-specific project description */
    private       JavaProjectDescription description;

    protected JavaProject(EventBus eventBus,
                          AsyncRequestFactory asyncRequestFactory,
                          ProjectServiceClient projectServiceClient,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(eventBus, asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.description = new JavaProjectDescription(this);
    }

    /** {@inheritDoc} */
    @Override
    public JavaProjectDescription getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(final Folder root, final AsyncCallback<Folder> callback) {
        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(root.getPath(), new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(final Array<ItemReference> children) {
                final Folder folderToRefresh = (root instanceof Package && root.getParent() != null) ? root.getParent() : root;

                // project may contains sub-modules
                if (Project.TYPE.equals(folderToRefresh.getResourceType())) {
                    Unmarshallable<Array<ProjectDescriptor>> unmarshaller =
                            dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectDescriptor.class);
                    projectServiceClient.getModules(root.getPath(), new AsyncRequestCallback<Array<ProjectDescriptor>>(unmarshaller) {
                        @Override
                        protected void onSuccess(Array<ProjectDescriptor> modules) {
                            addChildren(folderToRefresh, children, modules, java.util.Collections.<TreeElement>emptyList(), callback);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            callback.onFailure(exception);
                        }
                    });
                } else if (folderToRefresh instanceof SourceFolder || folderToRefresh instanceof Package) {
                    Unmarshallable<TreeElement> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(TreeElement.class);
                    projectServiceClient.getTree(root.getPath(), -1, new AsyncRequestCallback<TreeElement>(unmarshaller) {
                        @Override
                        protected void onSuccess(TreeElement result) {
                            addChildren(root, children, Collections.<ProjectDescriptor>createArray(), result.getChildren(), callback);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            callback.onFailure(exception);
                        }
                    });
                } else {
                    addChildren(root, children, Collections.<ProjectDescriptor>createArray(),
                                java.util.Collections.<TreeElement>emptyList(), callback);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void addChildren(Folder rootFolder, Array<ItemReference> children, Array<ProjectDescriptor> modules,
                             List<TreeElement> tree, AsyncCallback<Folder> callback) {
        JavaModelUnmarshaller unmarshaller = new JavaModelUnmarshaller(rootFolder, (JavaProject)rootFolder.getProject(), eventBus,
                                                                       asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
        unmarshaller.unmarshalChildren(children, modules, tree);
        callback.onSuccess(unmarshaller.getPayload());
    }

    /**
     * Recursively looks for the {@link Resource}.
     *
     * @param rootFolder
     *         root folder
     * @param path
     *         resource's path to find (e.g. /project/folder/file)
     * @param callback
     *         callback
     */
    public void findResourceByPath(Folder rootFolder, final String path, final AsyncCallback<Resource> callback) {
        // Avoid redundant requests. Use cached project structure.
        if (!rootFolder.getChildren().isEmpty()) {
            for (Resource child : rootFolder.getChildren().asIterable()) {
                if (path.equals(child.getPath())) {
                    callback.onSuccess(child);
                    return;
                } else if (path.startsWith(child.getPath().endsWith("/") ? child.getPath() : child.getPath() + "/")) {
                    // we're on the right way
                    findResourceByPath((Folder)child, path, callback);
                    return;
                } else if (child instanceof Package && child.getName().contains(".")) {
                    // this is 'compact' package
                    if (child.getPath().startsWith(path)) {
                        Package p = new Package();
                        p.setProject(JavaProject.this);
                        p.setParent(child.getParent());
                        final String name = path.substring(child.getParent().getPath().length() + 1).replace('/', '.');
                        p.setName(name);
                        callback.onSuccess(p);
                        return;
                    }
                }
            }
            callback.onFailure(new Exception("Resource not found"));
        } else {
            refreshChildren(rootFolder, new AsyncCallback<Folder>() {
                @Override
                public void onSuccess(Folder result) {
                    if (result.getChildren().isEmpty()) {
                        callback.onFailure(new Exception("Resource not found"));
                    } else {
                        findResourceByPath(result, path, callback);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }
            });
        }
    }

    /**
     * Create new Java package.
     * This method checks package name, and if name is not valid then calls
     * <code>onFailure</code> callback method with JavaModelException.
     *
     * @param parent
     *         the source folder where create package
     * @param name
     *         the name of new package e.g. com.codenvy
     * @param callback
     */
    public void createPackage(@NotNull final Folder parent, final String name, final AsyncCallback<Package> callback) {
        try {
            checkItemValid(parent);
            checkPackageName(name);

            final Folder checkedParent = checkParent(parent);
//            Folder foundParent = findFolderParent(checkedParent, name);
//            final Folder folderParent = foundParent == null ? checkedParent : foundParent;
            final Folder folderParent = checkedParent;

            projectServiceClient.createFolder(parent.getPath() + '/' + name.replace('.', '/'), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    final Package pack = new Package();
                    pack.setName(name);
                    pack.setParent(checkedParent);
                    pack.setProject(JavaProject.this);
                    checkedParent.addChild(pack);

                    // refresh tree, cause additional hierarchy folders may have been created
                    refreshChildren(folderParent, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
                            Resource folder = result.findChildByName(folderParent.getName());
                            if (folder != null) {
                                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(folder));
                            }
                            callback.onSuccess(pack);
                        }

                        @Override
                        public void onFailure(Throwable exception) {
                            callback.onFailure(exception);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Create new Compilation Unit (Java file).
     * Compilation unit may created only in Packages or SourceFolders.
     *
     * @param parent
     *         the parent, must be instance of Package or SourceFolder
     * @param name
     *         the name of new compilation unit
     * @param content
     *         the content of compilation unit
     * @param callback
     */
    public void createCompilationUnit(Folder parent, final String name, String content, final AsyncCallback<CompilationUnit> callback) {
        try {
            checkItemValid(parent);
            final Folder checkedParent = checkParent(parent);
            checkCompilationUnitName(name);

            projectServiceClient.createFile(parent.getPath(), name, content, MimeType.APPLICATION_JAVA, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    final CompilationUnit newCU = new CompilationUnit();
                    newCU.setName(name);
                    newCU.setMimeType(MimeType.APPLICATION_JAVA);
                    newCU.setParent(checkedParent);
                    newCU.setProject(JavaProject.this);
                    checkedParent.addChild(newCU);
                    // refresh tree, cause additional hierarchy folders my have been created
                    refreshChildren(checkedParent.getParent(), new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
                            eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newCU));
                            callback.onSuccess(newCU);
                        }

                        @Override
                        public void onFailure(Throwable exception) {
                            callback.onFailure(exception);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Find folder where package must be created, if such folder not exist then return null.
     *
     * @param parent
     * @param name
     * @return
     */
    protected Folder findFolderParent(Folder parent, String name) {
        Folder result = null;
        int longestMatch = 0;
        String[] newPackages = name.split("\\.");
        for (Resource r : parent.getChildren().asIterable()) {
            if (r instanceof Package) {
                if (name.startsWith(r.getName()) && r.getName().length() > longestMatch) {
                    // additional check for situation if parent package partial match:
                    // "com.codenvy.ide.cli" - exist, and we try to create "com.codenvy.ide.client" package,
                    // in this case parent folder for this package must be "com.codenvy.ide" not com.codenvy.ide.cli
                    String packName = r.getName();
                    String[] split = packName.split("\\.");
                    String lastPackage = split[split.length - 1];
                    if (newPackages.length > split.length && newPackages[split.length - 1].equals(lastPackage)) {
                        longestMatch = r.getName().length();
                        result = (Folder)r;
                    }
                }
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void createFolder(Folder parent, String name, AsyncCallback<Folder> callback) {
        if (parent instanceof JavaProject && description.getSourceFolders().contains(name)) {
            createSourceFolder((JavaProject)parent, name, callback);
            return;
        } else {
            try {
                checkPackageName(name);
                if (parent instanceof SourceFolder) {
                    createFolderAsPackage(parent, name, callback);
                    return;
                } else if (parent instanceof Package) {
                    createFolderAsPackage(parent.getParent(), name, callback);
                    return;
                }
            } catch (JavaModelException ignore) {
            }
        }

        super.createFolder(parent, name, callback);
    }

    private void createSourceFolder(final JavaProject parent, final String name, final AsyncCallback<Folder> callback) {
        final String folderName = parent.getPath() + '/' + name;
        projectServiceClient.createFolder(folderName, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                SourceFolder srcFolder = new SourceFolder();
                srcFolder.setName(name);
                // add to the list of items
                parent.addChild(srcFolder);
                // set proper parent project
                srcFolder.setProject(JavaProject.this);
                eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(srcFolder));
                callback.onSuccess(srcFolder);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void createFile(Folder parent, String name, String content, String mimeType, final AsyncCallback<File> callback) {
        if (parent instanceof SourceFolder || parent instanceof Package) {
            if (MimeType.APPLICATION_JAVA.equals(mimeType)) {
                createCompilationUnit(parent, name, content, new AsyncCallback<CompilationUnit>() {
                    @Override
                    public void onSuccess(CompilationUnit result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }
                });
                return;
            }

        }
        super.createFile(parent, name, content, mimeType, callback);
    }

    private void createFolderAsPackage(Folder parent, String name, final AsyncCallback<Folder> callback) {
        createPackage(parent, name.replaceAll("/", "."), new AsyncCallback<Package>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Package result) {
                callback.onSuccess(result);
            }
        });
    }

    /**
     * Checks that given <code>parent</code> is instance of {@link Package} or {@link SourceFolder}.
     *
     * @param parent
     *         folder to check
     * @return given folder as instance of {@link Package} or {@link SourceFolder}
     * @throws JavaModelException
     *         if the specified folder isn't {@link Package} or {@link SourceFolder}
     */
    protected Folder checkParent(Folder parent) throws JavaModelException {
        if (!(parent instanceof Package) && !(parent instanceof SourceFolder)) {
            for (SourceFolder sourceFolder : getSourceFolders().asIterable()) {
                if (parent.getPath().equals(sourceFolder.getPath())) {
                    return sourceFolder;
                } else if (parent.getPath().startsWith(sourceFolder.getPath())) {
                    String name = parent.getPath().replaceFirst(sourceFolder.getPath(), "");
                    name = (name.startsWith("/")) ? name.replaceFirst("/", "").replaceAll("/", ".") : name.replaceAll("/", ".");

                    parent = new Package();
                    parent.setName(name);
                    parent.setParent(sourceFolder);
                    parent.setProject(this);
                    return parent;
                }
            }
            throw new JavaModelException("CompilationUnit or Package must be child of 'Package' or 'SourceFolder'");
        }
        return parent;
    }

    /**
     * Get SourceFolder's in this project
     *
     * @return the array of source folders
     */
    public Array<SourceFolder> getSourceFolders() {
        Array<SourceFolder> sourceFolders = Collections.createArray();
        getSourceFolders(sourceFolders, getChildren());
        return sourceFolders;
    }

    private void getSourceFolders(Array<SourceFolder> sourceFolders, Array<Resource> children) {
        for (Resource r : children.asIterable()) {
            if (r instanceof SourceFolder) {
                sourceFolders.add((SourceFolder)r);
                return;
            } else if (r instanceof Folder) {
                getSourceFolders(sourceFolders, ((Folder)r).getChildren());
            }
        }
    }

}
