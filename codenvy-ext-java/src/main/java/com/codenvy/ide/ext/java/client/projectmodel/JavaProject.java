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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.TreeElement;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.java.jdt.core.JavaConventions;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Link;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.runtime.IStatus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

/**
 * A Java project represents a view of a project resource in terms of Java
 * elements such as package , compilation units.
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

    /** @param eventBus */
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
    public void refreshTree(final Folder root, final AsyncCallback<Folder> callback) {
        final Folder folderToRefresh = (root instanceof Package && root.getParent() != null) ? root.getParent() : root;

        projectServiceClient.getTree(root.getPath(), -1,
                                     new AsyncRequestCallback<TreeElement>(dtoUnmarshallerFactory.newUnmarshaller(TreeElement.class)) {
                                         @Override
                                         protected void onSuccess(TreeElement result) {
                                             JavaModelUnmarshaller unmarshaller =
                                                     new JavaModelUnmarshaller(folderToRefresh,
                                                                               (JavaProject)folderToRefresh.getProject(),
                                                                               eventBus,
                                                                               asyncRequestFactory,
                                                                               projectServiceClient,
                                                                               dtoUnmarshallerFactory);
                                             unmarshaller.unmarshal(result);
                                             callback.onSuccess(unmarshaller.getPayload());
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             callback.onFailure(exception);
                                         }
                                     });
    }

    /**
     * Create new Java package.
     * This method check package name, and if name not valid call <code>onFailure</code> callback method with
     * JavaModelException
     *
     * @param parent
     *         the source folder where create package
     * @param name
     *         the name of new package
     * @param callback
     */
    public void createPackage(@NotNull final Folder parent, final String name, final AsyncCallback<Package> callback) {
        try {
            checkItemValid(parent);
            final Folder checkedParent = checkParent(parent);
            if (!checkPackageName(name)) {
                callback.onFailure(new JavaModelException("Package name not valid"));
                return;
            }
            Folder foundParent = findFolderParent(checkedParent, name);
            final Folder folderParent = foundParent == null ? checkedParent : foundParent;

            projectServiceClient.createFolder(parent.getPath() + '/' + name, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    final Package pack = new Package();
                    pack.setName(name);

                    pack.setParent(checkedParent);
                    pack.setProject(JavaProject.this);
                    checkedParent.addChild(pack);
                    // TODO workaround for a unified view for packages
                    // SourceFolder sourceFolder = getSourceFolder(pack);
                    // refresh tree, cause additional hierarchy folders my have been created
                    refreshTree(folderParent, new AsyncCallback<Folder>() {
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
                            exception.printStackTrace();
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
     * Return a source folder for a chosen resource.
     *
     * @param folder
     *         folder for which a source folder needs to be found
     * @return a source folder
     */
    private SourceFolder getSourceFolder(@NotNull Folder folder) {
        if (folder instanceof SourceFolder) {
            return (SourceFolder)folder;
        }
        return getSourceFolder(folder.getParent());
    }

    /**
     * Create new Compilation Unit (Java file).
     * Compilation unit may created only in Packages or SourceFolder's.
     *
     * @param parent
     *         the parent, must be instance of Package or SourceFolder.
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
                    // TODO workaround for a unified view for packages
                    SourceFolder sourceFolder = getSourceFolder(newCU.getParent());
                    // refresh tree, cause additional hierarchy folders my have been created
                    refreshTree(checkedParent, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
                            eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newCU));
                            callback.onSuccess(newCU);
                        }

                        @Override
                        public void onFailure(Throwable exception) {
                            exception.printStackTrace();
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
                    //additional check for situation if parent package partial match:
                    // "com.exo.ide.cli" - exist, and we try to create "com.exo.ide.client" package,
                    //in this case parent folder for this package must be "com.exo.ide" not com.exo.ide.cli
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
        } else if (checkPackageName(name)) {
            if (parent instanceof SourceFolder) {
                createFolderAsPackage(parent, name, callback);
                return;
            } else if (parent instanceof Package) {
                createFolderAsPackage(parent.getParent(), name, callback);
                return;
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
        createPackage((SourceFolder)parent, name.replaceAll("/", "."), new AsyncCallback<Package>() {
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
     * Check is parent instance of Package or Source folder
     *
     * @param parent
     * @throws JavaModelException
     */
    protected Folder checkParent(Folder parent) throws JavaModelException {
        if (!(parent instanceof Package) && !(parent instanceof SourceFolder)) {
            for (SourceFolder sourceFolder : getSourceFolders().asIterable()) {
                if (parent.getPath().equals(sourceFolder.getPath())) {
                    return sourceFolder;
                } else if (parent.getPath().startsWith(sourceFolder.getPath())) {
                    String id = parent.getId();
                    String name = parent.getPath().replaceFirst(sourceFolder.getPath(), "");
                    name = (name.startsWith("/")) ? name.replaceFirst("/", "").replaceAll("/", ".") : name.replaceAll("/", ".");
                    StringMap<Link> links = parent.getLinks();
                    parent = new Package();
                    parent.setId(id);
                    parent.setName(name);
                    parent.setParent(sourceFolder);
                    parent.setProject(this);
                    parent.getLinks().putAll(links);
                    return parent;
                }
            }
            throw new JavaModelException("CompilationUnit or Package must be child of 'Package' or 'SourceFolder'");
        }
        return parent;
    }

    /**
     * Check package name.
     * <p/>
     * The syntax of a package name corresponds to PackageName as
     * defined by PackageDeclaration (JLS2 7.4). For example, <code>"java.lang"</code>.
     * <p/>
     *
     * @param name
     */
    protected boolean checkPackageName(String name) {
        //TODO infer COMPILER_SOURCE and COMPILER_COMPLIANCE to project properties
        IStatus status =
                JavaConventions.validatePackageName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                    JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
        return status.getSeverity() != IStatus.ERROR;

    }

    /**
     * Check the given compilation unit name.
     * <p>
     * A compilation unit name must obey the following rules:
     * <ul>
     * <li> it must not be null
     * <li> it must be suffixed by a dot ('.') followed by one of the java like extension
     * <li> its prefix must be a valid identifier
     * </ul>
     * </p>
     *
     * @param name
     * @throws JavaModelException
     */
    private void checkCompilationUnitName(String name) throws JavaModelException {
        //TODO infer COMPILER_SOURCE and COMPILER_COMPLIANCE to project properties
        IStatus status =
                JavaConventions.validateCompilationUnitName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
        if (status.getSeverity() == IStatus.ERROR) {
            throw new JavaModelException(status.getMessage());
        }
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
