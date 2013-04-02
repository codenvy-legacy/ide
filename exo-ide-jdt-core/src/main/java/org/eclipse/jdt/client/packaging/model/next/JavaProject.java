/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eclipse.jdt.client.packaging.model.next;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class JavaProject extends IDEProject {

    private class UpdateManager {

        private ArrayList<Command> commands = new ArrayList<Command>();

        private AsyncCallback<Boolean> callback;

        public void addCommand(Command command) {
            commands.add(command);
        }

        public void update(AsyncCallback<Boolean> callback) {
            this.callback = callback;
            updateNext();
        }

        public void updateNext() {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    if (commands.isEmpty()) {
                        callback.onSuccess(true);
                        return;
                    }

                    commands.remove(0).execute();
                }
            });
        }

        public void updateFailed(final Throwable caught) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    commands.clear();
                    callback.onFailure(caught);
                }
            });
        }

    }

    private PomXml pom;

    private List<ProjectModel> modules = new ArrayList<ProjectModel>();

    private List<SourceDirectory> sourceDirectories = new ArrayList<SourceDirectory>();

    private List<ClasspathFolder> classpathFolders = new ArrayList<ClasspathFolder>();

    public JavaProject(ProjectModel project) {
        super(project);
    }

    public List<ProjectModel> getModules() {
        return modules;
    }

    public List<SourceDirectory> getSourceDirectories() {
        return sourceDirectories;
    }

    public List<ClasspathFolder> getClasspathFolders() {
        return classpathFolders;
    }

    private FolderChangedHandler originalFolderChangedHandler;

    @Override
    public void setFolderChangedHandler(FolderChangedHandler folderChangedHandler) {
        originalFolderChangedHandler = folderChangedHandler;
        super.setFolderChangedHandler(this.folderChangedHandler);
    }

    private FolderChangedHandler folderChangedHandler = new FolderChangedHandler() {
        @Override
        public void onFolderChanged(final FolderModel folder) {
            UpdateManager updateManager = new UpdateManager();
            update(updateManager);
            updateManager.update(new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable caught) {
                    System.out.println("Update " + JavaProject.this.getPath() + " failed");
                    caught.printStackTrace();

                    if (originalFolderChangedHandler != null) {
                        originalFolderChangedHandler.onFolderChanged(folder);
                    }
                }

                @Override
                public void onSuccess(Boolean result) {
                    if (originalFolderChangedHandler != null) {
                        originalFolderChangedHandler.onFolderChanged(folder);
                    }
                }
            });
        }
    };

    @Override
    public void refresh(FolderModel folder, final AsyncCallback<Folder> callback) {
        super.refresh(folder, new AsyncCallback<Folder>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(final Folder resultFolder) {
                UpdateManager updateManager = new UpdateManager();
                update(updateManager);

                updateManager.update(new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        callback.onSuccess(resultFolder);
                    }
                });
            }
        });
    }

    protected void update(final UpdateManager updateManager) {
        updateManager.addCommand(new Command() {
            @Override
            public void execute() {
                // Search pom.xml
                FileModel pomXmlFile = null;
                for (Item item : JavaProject.this.getChildren().getItems()) {
                    if ("pom.xml".equals(item.getName()) && item instanceof FileModel) {
                        pomXmlFile = (FileModel)item;
                    }
                }

                if (pomXmlFile == null) {
                    pom = null;
                    modules.clear();
                    sourceDirectories.clear();
                    classpathFolders.clear();
                    updateManager.updateNext();
                    return;
                }

                if (pom == null) {
                    pom = new PomXml(pomXmlFile);
                } else if (!pom.getPomFile().getId().equals(pomXmlFile.getId())) {
                    pom.setPomFile(pomXmlFile);
                }

                try {
                    pom.refresh(new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            updateManager.updateFailed(caught);
                        }

                        @Override
                        public void onSuccess(Boolean result) {
                            updateProjectStructure(updateManager);
                            updateManager.updateNext();
                        }
                    });
                } catch (Exception e) {
                    updateManager.updateFailed(e);
                }
            }
        });
    }

    private void updateProjectStructure(final UpdateManager updateManager) {
        updateManager.addCommand(new Command() {
            @Override
            public void execute() {
                updateModules(updateManager);
                updateSourceDirectories();
                updateProjectReferences();

                updateManager.updateNext();
            }
        });
    }

    private void updateModules(final UpdateManager updateManager) {
        getModules().clear();

        for (Item item : getChildren().getItems()) {
            if (item instanceof JavaProject) {
                JavaProject javaProject = (JavaProject)item;
                getModules().add(javaProject);
                javaProject.update(updateManager);
            }
        }
    }

    private void updateSourceDirectories() {
        sourceDirectories.clear();

        for (String dir : pom.getSourceDirectories()) {
            try {
                Item item = getResource(this, dir);
                if (item instanceof FolderModel) {
                    SourceDirectory sourceDirectory = new SourceDirectory((FolderModel)item, dir);
                    sourceDirectories.add(sourceDirectory);
                    updatePackages(sourceDirectory);
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
            }
        }
    }

    private void updatePackages(SourceDirectory sourceDirectory) throws Exception {
        sourceDirectory.getPackages().clear();

        // Add default package
        Package defaultPackage = new Package(sourceDirectory, "");
        sourceDirectory.getPackages().add(defaultPackage);

        List<FolderModel> folders = scanFolders(sourceDirectory);
        for (FolderModel folder : folders) {
            String packageName = getPackageName(folder.getPath(), sourceDirectory.getSourceDirectoryName());
            Package pack = new Package(folder, packageName);
            sourceDirectory.getPackages().add(pack);
        }

        for (Package pack : sourceDirectory.getPackages()) {
            for (Item item : pack.getChildren().getItems()) {
                if (item instanceof FileModel) {
                    pack.getFiles().add((FileModel)item);
                }
            }
        }

    }

    private String getPackageName(String folderPath, String sourceDirectory) {
        while (sourceDirectory.startsWith("/")) {
            sourceDirectory = sourceDirectory.substring(1);
        }

        while (sourceDirectory.endsWith("/")) {
            sourceDirectory = sourceDirectory.substring(0, sourceDirectory.length() - 1);
        }

        String packageName = folderPath.substring((getPath() + "/" + sourceDirectory + "/").length());
        return packageName.replaceAll("/", ".");
    }

    private List<FolderModel> scanFolders(FolderModel folder) throws Exception {
        List<Item> items = new ArrayList<Item>();
        items.addAll(folder.getChildren().getItems());

        List<FolderModel> folders = new ArrayList<FolderModel>();
        for (Item item : items) {
            if (item instanceof FolderModel) {
                folders.add((FolderModel)item);
                folders.addAll(scanFolders((FolderModel)item));
            }
        }
        return folders;
    }

    private void updateProjectReferences() {
        classpathFolders.clear();

        ClasspathFolder classpathFolder = new ClasspathFolder("Maven Dependencies");
        classpathFolders.add(classpathFolder);

        List<String> dependencies = pom.getMavenDependencies();
        for (String dependency : dependencies) {
            Classpath classpath = new Classpath(dependency);
            classpathFolder.getClasspathList().add(classpath);
        }
    }

    @Override
    public void resourceChanged(Item resource) {
        super.resourceChanged(resource);

        if (resource instanceof FileModel) {
            FileModel file = (FileModel)resource;
            if ("pom.xml".equals(resource.getName())) {
                folderChangedHandler.onFolderChanged(file.getParent());
            }
        }
    }

}
