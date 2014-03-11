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
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.TreeElement;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;
import com.codenvy.ide.collections.StringSet.IterationCallback;
import com.codenvy.ide.ext.java.jdt.core.JavaConventions;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

/**
 * Recursively traverses the {@link TreeElement} to build Java project model.
 *
 * @author Evgen Vidolob
 */
public class JavaModelUnmarshaller {

    private final AsyncRequestFactory    asyncRequestFactory;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private       JavaProject            project;
    private       StringSet              sourceFolders;
    private       String                 projectPath;
    private       Folder                 root;
    private       EventBus               eventBus;

    public JavaModelUnmarshaller(Folder root, JavaProject project, EventBus eventBus, AsyncRequestFactory asyncRequestFactory,
                                 ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super();
        this.root = root;
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.root.getChildren().clear();
        this.project = project;

        sourceFolders = Collections.createStringSet();
        projectPath = project.getPath();
        project.getDescription().getSourceFolders().iterate(new IterationCallback() {
            @Override
            public void onIteration(String key) {
                sourceFolders.add(projectPath + (key.startsWith("/") ? key : "/" + key));
            }
        });
    }

    public void unmarshal(TreeElement response) {
        parseProjectStructure(response.getChildren(), root, root, project);
    }

    /**
     * Parse project structure and build Java project model.
     *
     * @param children
     *         children to parse
     * @param parentFolder
     *         the folder to add children's that part of java model
     * @param parentFolderNonModelItems
     *         the folder to add children's that not part of java model
     * @param project
     *         the project for that building java model
     */
    private void parseProjectStructure(List<TreeElement> children, Folder parentFolder, Folder parentFolderNonModelItems, Project project) {
        for (TreeElement itemObject : children) {
            ItemReference item = itemObject.getNode();
            String itemName = item.getName();

            final String type = item.getType();

            if (Project.TYPE.equalsIgnoreCase(type)) {
                Project childProject;
                Resource existingProject = parentFolder.findChildByName(itemName);
                if (existingProject == null) {
                    existingProject = project.findChildByName(itemName);
                }
                if (existingProject == null) {
                    existingProject = parentFolderNonModelItems.findChildByName(itemName);
                }

                // Make sure found resource is Project
                if (existingProject != null && existingProject instanceof Project) {
                    // use existing folder instance as is
                    childProject = (Project)existingProject;
                    childProject.getChildren().clear();
                    parseProjectStructure(itemObject.getChildren(), childProject, childProject, childProject);
                } else {
                    childProject = new JavaProject(eventBus, asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
                    childProject.init(itemObject.getNode());
                    parentFolderNonModelItems.addChild(childProject);
                    childProject.setProject(childProject);
                    parseProjectStructure(itemObject.getChildren(), childProject, childProject, childProject);
                }
            } else if (Folder.TYPE.equalsIgnoreCase(type)) {
                Folder folder;
                Resource existingFolder = parentFolder.findChildByName(itemName);
                if (existingFolder == null) {
                    existingFolder = project.findChildByName(itemName);
                }
                if (existingFolder == null) {
                    existingFolder = parentFolderNonModelItems.findChildByName(itemName);
                }

                // Make sure found resource is Folder
                if (existingFolder != null && existingFolder instanceof Folder) {
                    // use existing folder instance as is
                    folder = (Folder)existingFolder;
                    folder.getChildren().clear();
                    if (folder instanceof Package) {
                        parseProjectStructure(itemObject.getChildren(), folder.getParent(), folder, project);
                    } else {
                        parseProjectStructure(itemObject.getChildren(), folder, folder, project);
                    }
                } else {
                    final String path = item.getPath();
                    final String name = item.getName();
                    // create new source folder
                    if (sourceFolders.contains(path)) {
                        folder = new SourceFolder(item, name);
                        parentFolder.addChild(folder);
                        folder.setProject(project);
                        sourceFolders.remove(path);
                        parseProjectStructure(itemObject.getChildren(), folder, folder, project);
                    }
                    // add package or regular folder
                    else {
                        String packageName = name;
                        // filter folders with invalid names for java packages
                        if ((parentFolder instanceof SourceFolder || parentFolder instanceof Package) && isPackageNameValid(packageName)) {
                            if (parentFolder instanceof SourceFolder) {
                                sourceFolders.remove(path);
                            }
                            packageName = path.replace(parentFolder.getPath(), "").replaceAll("/", ".");
                            packageName = packageName.startsWith(".") ? packageName.replaceFirst(".", "") : packageName;
                            folder = new Package(item, packageName);

                            if (itemObject.getChildren().size() == 1
                                && itemObject.getChildren().get(0).getNode().getType().equalsIgnoreCase(Folder.TYPE)) {
                                parseProjectStructure(itemObject.getChildren(), parentFolder, folder, project);
                            } else {
                                parentFolder.addChild(folder);
                                folder.setProject(project);
                                parseProjectStructure(itemObject.getChildren(), folder, folder, project);
                            }
                        } else {
                            folder = new Folder(item);
                            parentFolderNonModelItems.addChild(folder);
                            folder.setProject(project);
                            parseProjectStructure(itemObject.getChildren(), folder, folder, project);
                        }
                    }
                }
            } else if (File.TYPE.equalsIgnoreCase(type)) {
                File file;
                // check if parent of this file is package and file has valid java name,
                // then add as compilation unit else as regular file
                if (parentFolderNonModelItems instanceof Package && isCompilationUnitName(item.getName())) {
                    file = new CompilationUnit(item);
                } else {
                    file = new File(item);
                }
                parentFolderNonModelItems.addChild(file);
                file.setProject(project);
            } else {
                Log.error(this.getClass(), "Unsupported Resource type: " + type);
            }
        }
    }

    private boolean isPackageNameValid(String name) {
        IStatus status = JavaConventions.validatePackageName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                             JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
        switch (status.getSeverity()) {
            case Status.WARNING:
            case Status.OK:
                return true;
            default:
                return false;
        }
    }

    private boolean isCompilationUnitName(String name) {
        IStatus status = JavaConventions.validateCompilationUnitName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                                     JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
        switch (status.getSeverity()) {
            case Status.WARNING:
            case Status.OK:
                return true;
            default:
                return false;
        }
    }

    private void getProjectDescriptor(String path, final AsyncCallback callback) {
        projectServiceClient.getProject(path, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    public Folder getPayload() {
        return root;
    }

}
