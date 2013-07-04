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
package org.eclipse.jdt.client;

import com.codenvy.ide.json.shared.JsonCollections;
import com.codenvy.ide.json.shared.JsonStringSet;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.create.CreateJavaClassPresenter;
import org.eclipse.jdt.client.event.PackageCreatedEvent;
import org.eclipse.jdt.client.event.PackageCreatedHandler;
import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PackagesUpdater implements ProjectOpenedHandler, FileSavedHandler, ProjectClosedHandler,
                                        PackageCreatedHandler {

    private static final int DALAY = 5000;

    private static final int DALAY_LONG = 1000 * 60;

    private static final int MAX_REQUEST = 10;

    private final HandlerManager eventBus;

    private final SupportedProjectResolver projectResolver;

    private HandlerRegistration saveFileHandler;

    private final TypeInfoStorage storage;

    private int requestCount = 0;

    //   private String projectId;

    private boolean canSchedule = false;

    private int delay = DALAY_LONG;

    public PackagesUpdater(HandlerManager eventBus, SupportedProjectResolver projectResolver, TypeInfoStorage storage) {
        this.eventBus = eventBus;
        this.projectResolver = projectResolver;
        this.storage = storage;
        eventBus.addHandler(ProjectOpenedEvent.TYPE, this);
        eventBus.addHandler(ProjectClosedEvent.TYPE, this);
        eventBus.addHandler(PackageCreatedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event
     * .FileSavedEvent) */
    @Override
    public void onFileSaved(FileSavedEvent event) {
        if ("pom.xml".equals(event.getFile().getName())) {
            delay = DALAY_LONG;
            requestCount = 0;
            timer.cancel();
            timer.schedule(delay);
        }
    }

    private Timer timer = new Timer() {
        @Override
        public void run() {
            updatePackages(ids);
        }
    };

    private ArrayList<String> ids;

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        ProjectModel project = event.getProject();
        requestCount = 0;
        ids = new ArrayList<String>();

        if (project instanceof JavaProject &&
            project.getProjectType().equals(ProjectType.MultiModule.value())) {
            JavaProject javaProject = (JavaProject)project;

            List<ProjectModel> children = javaProject.getModules();
            for (ProjectModel item : children) {
                if (projectResolver.isProjectSupported(item.getProjectType())) {
                    ids.add(item.getId());
                }
            }
        } else if (projectResolver.isProjectSupported(project.getProjectType())) {
            ids.add(project.getId());
        }

        updatePackages(ids);
    }

    private void updatePackages(ArrayList<String> ids) {
        timer.cancel();
        saveFileHandler = eventBus.addHandler(FileSavedEvent.TYPE, this);
        for (String id : ids) {
            updatePackages(id);
        }
        delay = DALAY;
//      timer.schedule(delay);
        schedule();
    }

    private void schedule() {
        requestCount++;
        if (requestCount < MAX_REQUEST) {
            timer.schedule(delay);
        }
    }

    /** @param projectId */
    private void updatePackages(final String projectId) {
        String url =
                Utils.getRestContext() + Utils.getWorkspaceName() + "/code-assistant/java/get-packages" + "?projectid=" + projectId + "&vfsid="
                + VirtualFileSystem.getInstance().getInfo().getId();
        try {
            AsyncRequest.build(RequestBuilder.GET, url).send(
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                        @Override
                        protected void onSuccess(StringBuilder result) {
                            JSONArray arr = JSONParser.parseLenient(result.toString()).isArray();
                            JsonStringSet stringSet = JsonCollections.createStringSet();
                            for (int i = 0; i < arr.size(); i++) {
                                stringSet.add(arr.get(i).isString().stringValue());
                            }
                            storage.setPackages(projectId, stringSet);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            timer.cancel();
//                  IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
        }
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        timer.cancel();
        if (saveFileHandler != null) {
            saveFileHandler.removeHandler();
            saveFileHandler = null;
        }
    }

    /** @see org.eclipse.jdt.client.event.PackageCreatedHandler#onPackageCreated(org.eclipse.jdt.client.event.PackageCreatedEvent) */
    @Override
    public void onPackageCreated(PackageCreatedEvent event) {
        FolderModel parentFolder = event.getParentFolder();
        ProjectModel project = parentFolder.getProject();
        String sourcePath =
                project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder")
                                                    : CreateJavaClassPresenter.DEFAULT_SOURCE_FOLDER;
        String path = project.getPath() + "/" + sourcePath;
        String pack = "";
        if (!path.equals(parentFolder.getPath()))
            pack = parentFolder.getPath().substring(path.length() + 1);
        pack = pack.replaceAll("\\\\", ".");
        String newPackage = event.getPack();
        if (newPackage.contains(".")) {
            String[] packageFragments = newPackage.split("\\.");
            StringBuilder builder = new StringBuilder(pack);
            for (String fragment : packageFragments) {
                if (builder.length() != 0)
                    builder.append('.');
                builder.append(fragment);
                storage.getPackages(project.getId()).add(builder.toString());
            }
        } else
            storage.getPackages(project.getId()).add(pack + '.' + newPackage);

    }

}
