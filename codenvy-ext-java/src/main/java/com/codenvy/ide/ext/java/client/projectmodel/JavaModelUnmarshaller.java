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
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;
import com.codenvy.ide.collections.StringSet.IterationCallback;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdt.core.JavaConventions;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Recursively traverses the JSON Response to build Java project model
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class JavaModelUnmarshaller implements Unmarshallable<Folder> {

    private static final String CHILDREN = "children";
    private static final String TYPE     = "itemType";
    private static final String ITEM     = "item";
    private static final String ID       = "id";
    private static final String PATH     = "path";
    private static final String NAME     = "name";
    private final AsyncRequestFactory  asyncRequestFactory;
    private final ProjectServiceClient projectServiceClient;
    private       JavaProject          project;
    private       StringSet            sourceFolders;
    private       String               projectPath;
    private       Folder               root;
    private       EventBus             eventBus;

    public JavaModelUnmarshaller(Folder root, JavaProject project, EventBus eventBus, AsyncRequestFactory asyncRequestFactory,
                                 ProjectServiceClient projectServiceClient) {
        super();
        this.root = root;
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
        this.projectServiceClient = projectServiceClient;
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

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONObject object = JSONParser.parseLenient(response.getText()).isObject();
            parseProjectStructure(object.get(CHILDREN), root, root, project);
        } catch (Exception exc) {
            String message = "Can't parse response " + response.getText();
            throw new UnmarshallerException(message, exc);
        }
    }

    /**
     * Parse project structure and build Java project model
     *
     * @param children
     *         the json array to parse
     * @param parentFolder
     *         the folder to add children's that part of java model
     * @param parentFolderNonModelItems
     *         the folder to add children's that not part of java model
     * @param project
     *         the project for that building java model
     */
    private void parseProjectStructure(JSONValue children, Folder parentFolder, Folder parentFolderNonModelItems, Project project) {
        JSONArray itemsArray = children.isArray();
        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject itemObject = itemsArray.get(i).isObject();
            // Get item
            JSONObject item = itemObject.get(ITEM).isObject();
            String id = item.get(ID).isString().stringValue();
            //
            String type = null;
            if (item.get(TYPE).isNull() == null) {
                type = item.get(TYPE).isString().stringValue();
            }

            // Project found in JSON Response
            if (Project.TYPE.equalsIgnoreCase(type)) {
                Project childProject;
                Resource existingProject = parentFolder.findChildById(id);
                if (existingProject == null) {
                    existingProject = project.findChildById(id);
                }
                if (existingProject == null) {
                    existingProject = parentFolderNonModelItems.findChildById(id);
                }

                // Make sure found resource is Project
                if (existingProject != null && existingProject instanceof Project) {
                    // use existing folder instance as
                    childProject = (Project)existingProject;
                    childProject.getChildren().clear();
                    parseProjectStructure(itemObject.get(CHILDREN), childProject, childProject, childProject);
                } else {
                    childProject = new JavaProject(eventBus, asyncRequestFactory, projectServiceClient);
                    childProject.init(itemObject.get(ITEM).isObject());
                    parentFolderNonModelItems.addChild(childProject);
                    childProject.setProject(childProject);
                    parseProjectStructure(itemObject.get(CHILDREN), childProject, childProject, childProject);
                }

            }
            // Folder
            else if (Folder.TYPE.equalsIgnoreCase(type)) {
                Folder folder;
                Resource existingFolder = parentFolder.findChildById(id);
                if (existingFolder == null) {
                    existingFolder = project.findChildById(id);
                }
                if (existingFolder == null) {
                    existingFolder = parentFolderNonModelItems.findChildById(id);
                }

                // Make sure found resource is Folder
                if (existingFolder != null && existingFolder instanceof Folder) {
                    // use existing folder instance as
                    folder = (Folder)existingFolder;
                    folder.getChildren().clear();
                    if (folder instanceof Package) {
                        parseProjectStructure(itemObject.get(CHILDREN), folder.getParent(), folder, project);
                    } else {
                        parseProjectStructure(itemObject.get(CHILDREN), folder, folder, project);
                    }
                } else {
                    String path = item.get(PATH).isString().stringValue();
                    String name = item.get(NAME).isString().stringValue();
                    // create new source folder
                    if (sourceFolders.contains(path)) {
                        folder = new SourceFolder(item, name);
                        parentFolder.addChild(folder);
                        folder.setProject(project);
                        sourceFolders.remove(path);
                        parseProjectStructure(itemObject.get(CHILDREN), folder, folder, project);
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

                            if (itemObject.get(CHILDREN).isArray().size() == 1
                                && itemObject.get(CHILDREN).isArray().get(0).isObject().get(ITEM).isObject().get(TYPE).isString()
                                             .stringValue().equalsIgnoreCase(Folder.TYPE)) {
                                parseProjectStructure(itemObject.get(CHILDREN), parentFolder, folder, project);
                            } else {
                                parentFolder.addChild(folder);
                                folder.setProject(project);
                                parseProjectStructure(itemObject.get(CHILDREN), folder, folder, project);
                            }
                        } else {
                            folder = new Folder(item);
                            parentFolderNonModelItems.addChild(folder);
                            folder.setProject(project);
                            parseProjectStructure(itemObject.get(CHILDREN), folder, folder, project);
                        }
                    }
                }
            }
            // File
            else if (File.TYPE.equalsIgnoreCase(type)) {
                File file;
                // check if parent of this file is package and file has valid java name,
                // then add as compilation unit else as regular file
                if (parentFolderNonModelItems instanceof Package
                    && isCompilationUnitName(item.get(NAME).isString().stringValue())) {
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

    /** {@inheritDoc} */
    @Override
    public Folder getPayload() {
        return root;
    }

}
