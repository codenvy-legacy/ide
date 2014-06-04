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
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;
import com.codenvy.ide.collections.StringSet.IterationCallback;
import com.codenvy.ide.ext.java.jdt.core.JavaConventions;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.util.loging.Log;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

/**
 * Recursively traverses the folder's children to build Java project model.
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

    public void unmarshalChildren(Array<ItemReference> children, Array<ProjectDescriptor> modules) {
        parseChildren(children, root, root, modules, project);
    }

    public void unmarshalTree(List<TreeElement> children) {
        for (TreeElement treeElement : children) {
            ItemReference item = treeElement.getNode();
            if (item.getType().equals(Folder.TYPE) && treeElement.getChildren().size() == 1 && item.getChildrenCount() == 1) {
                unmarshalTree(treeElement.getChildren());
            } else {
                final String itemPath = item.getPath();
                String packageName = itemPath.replace(root.getPath(), "").replaceAll("/", ".");
                packageName = packageName.startsWith(".") ? packageName.replaceFirst(".", "") : packageName;
                parseFolderItem(item, root, new Package(item, packageName), project);
            }
        }
    }

    private void parseChildren(Array<ItemReference> children, Folder parentFolder, Folder parentFolderNonModelItems,
                               Array<ProjectDescriptor> modules, Project project) {
        for (ItemReference item : children.asIterable()) {
            // skip hidden items
            if (item.getName().startsWith(".")) {
                continue;
            }

            switch (item.getType()) {
                case File.TYPE:
                    File file;
                    // check if parent of this file is package and file has valid JAVA name,
                    // then add as compilation unit else as regular file
                    if (parentFolderNonModelItems instanceof Package && isCompilationUnitName(item.getName())) {
                        file = new CompilationUnit(item);
                    } else {
                        file = new File(item);
                    }
                    parentFolderNonModelItems.addChild(file);
                    file.setProject(project);
                    break;
                case Folder.TYPE:
                    if (isProject(item, modules)) {
                        parseProjectItem(item, parentFolder, parentFolderNonModelItems, project, modules);
                    } else {
                        parseFolderItem(item, parentFolder, parentFolderNonModelItems, project);
                    }
                    break;
                default:
                    Log.error(this.getClass(), "Unsupported resource type: " + item.getType());
            }
        }
    }

    private boolean isProject(ItemReference item, Array<ProjectDescriptor> modules) {
        for (ProjectDescriptor moduleDescriptor : modules.asIterable()) {
            if (moduleDescriptor.getName().equals(item.getName())) {
                return true;
            }
        }
        return false;
    }

    private void parseProjectItem(ItemReference item, Folder parentFolder, Folder parentFolderNonModelItems, Project project,
                                  Array<ProjectDescriptor> modules) {
        Project childProject;
        Resource existingProject = parentFolder.findChildByName(item.getName());
        if (existingProject == null) {
            existingProject = project.findChildByName(item.getName());
        }
        if (existingProject == null) {
            existingProject = parentFolderNonModelItems.findChildByName(item.getName());
        }

        // Make sure found resource is Project
        if (existingProject != null && existingProject instanceof Project) {
            // use existing folder instance as is
            childProject = (Project)existingProject;
            childProject.getChildren().clear();
        } else {
            childProject = new JavaProject(eventBus, asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
            childProject.init(item);

            for (ProjectDescriptor moduleDescriptor : modules.asIterable()) {
                if (moduleDescriptor.getName().equals(item.getName())) {
                    childProject.setAttributes(moduleDescriptor.getAttributes());
                    childProject.setProjectType(moduleDescriptor.getProjectTypeId());
                    break;
                }
            }

            parentFolderNonModelItems.addChild(childProject);
            childProject.setProject(childProject);
        }
    }

    private void parseFolderItem(ItemReference item, Folder parentFolder, Folder parentFolderNonModelItems, Project project) {
        Folder folder;
        Resource existingFolder = parentFolder.findChildByName(item.getName());
        if (existingFolder == null) {
            existingFolder = project.findChildByName(item.getName());
        }
        if (existingFolder == null) {
            existingFolder = parentFolderNonModelItems.findChildByName(item.getName());
        }

        // Make sure found resource is Folder
        if (existingFolder != null && existingFolder instanceof Folder) {
            // use existing folder instance as is
            folder = (Folder)existingFolder;
            folder.getChildren().clear();
        } else {
            final String path = item.getPath();
            final String name = item.getName();
            // create new source folder
            if (sourceFolders.contains(path)) {
                folder = new SourceFolder(item, name);
                parentFolder.addChild(folder);
                folder.setProject(project);
                sourceFolders.remove(path);
            }
            // add package or regular folder
            else {
                String packageName = name;
                // filter folders with invalid names for java packages
                if ((parentFolder instanceof SourceFolder || parentFolder instanceof Package) &&
                    isPackageNameValid(packageName)) {
                    if (parentFolder instanceof SourceFolder) {
                        sourceFolders.remove(path);
                    }
                    packageName = path.replace(parentFolder.getPath(), "").replaceAll("/", ".");
                    packageName = packageName.startsWith(".") ? packageName.replaceFirst(".", "") : packageName;
                    folder = new Package(item, packageName);
                    parentFolder.addChild(folder);
                    folder.setProject(project);
                } else {
                    folder = new Folder(item);
                    parentFolderNonModelItems.addChild(folder);
                    folder.setProject(project);
                }
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

    public Folder getPayload() {
        return root;
    }

}
