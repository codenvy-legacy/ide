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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;
import com.codenvy.api.project.server.ProjectEvent;
import com.codenvy.api.project.server.ProjectEventListener;
import com.codenvy.api.project.server.ProjectEventService;
import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.api.vfs.server.observation.VirtualFileEvent;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.impl.AssistOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintenance and create JavaProjects
 *
 * @author Evgen Vidolob
 */
@Singleton
public class JavaProjectService {
    /** Logger. */
    private static final Logger LOG =
            LoggerFactory.getLogger(JavaProjectService.class);

    private ConcurrentHashMap<String, JavaProject> cache =
            new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, ConcurrentHashMap<String, ProjectEventListener>> eventListeners =
            new ConcurrentHashMap<>();

    private ProjectEventService projectEventService;
    private ProjectManager      projectManager;
    private String              tempDir;
    private Map<String, String> options = new HashMap<>();

    @Inject
    public JavaProjectService(ProjectEventService projectEventService, ProjectManager projectManager, EventService eventService,
                              @Named("project.temp") String temp) {
        this.projectEventService = projectEventService;
        this.projectManager = projectManager;
        tempDir = temp;
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.CORE_ENCODING, "UTF-8");
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_7);
        options.put(AssistOptions.OPTION_PerformVisibilityCheck, AssistOptions.ENABLED);
        options.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_TaskTags, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_SuppressWarnings, CompilerOptions.DISABLED);
        options.put(JavaCore.COMPILER_TASK_TAGS, "TODO,FIXME,XXX");
        options.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
        options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        options.put(CompilerOptions.OPTION_Process_Annotations, JavaCore.DISABLED);
        eventService.subscribe(new VirtualFileEventSubscriber());
    }

    public static void removeRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

    public JavaProject getOrCreateJavaProject(String wsId, String projectPath) {
        String key = wsId + projectPath;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        com.codenvy.api.project.server.Project project = projectManager.getProject(wsId, projectPath);
        JavaProject javaProject = new JavaProject(project, tempDir, projectManager, wsId, new HashMap<>(options));
        cache.put(key, javaProject);
        if (!eventListeners.containsKey(wsId)) {
            eventListeners.put(wsId, new ConcurrentHashMap<String, ProjectEventListener>());
        }
        ConcurrentHashMap<String, ProjectEventListener> map = eventListeners.get(wsId);
        if (!map.containsKey(projectPath)) {
            ProjectEventListenerImpl listener = new ProjectEventListenerImpl(wsId, projectPath);
            map.put(projectPath, listener);
            projectEventService.addListener(wsId, projectPath, listener);
        }
        return javaProject;
    }


    public void removeProject(String wsId, String projectPath) {
        JavaProject javaProject = cache.remove(wsId + projectPath);
        if (javaProject != null) {
            try {
                javaProject.close();
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, String> getOptions() {
        return options;
    }

    private class VirtualFileEventSubscriber implements EventSubscriber<VirtualFileEvent> {

        @Override
        public void onEvent(VirtualFileEvent event) {
            final VirtualFileEvent.ChangeType eventType = event.getType();
            final String eventWorkspace = event.getWorkspaceId();
            final String eventPath = event.getPath();
            if (eventType == VirtualFileEvent.ChangeType.DELETED) {
                if (cache.containsKey(eventWorkspace + eventPath)) {
                    JavaProject javaProject = cache.remove(eventWorkspace + eventPath);
                    javaProject.getIndexManager().deleteIndexFiles();
                    javaProject.getIndexManager().shutdown();
                    String vfsId = javaProject.getVfsId();
                    if (vfsId != null) {
                        File projectDepDir = new File(tempDir, vfsId);
                        if (projectDepDir.exists()) {
                            try {
                                removeRecursive(projectDepDir.toPath());
                            } catch (IOException e) {
                                LOG.error("Can't delete project dependency directory: " + projectDepDir.getPath());
                            }
                        }
                        if (eventListeners.containsKey(eventWorkspace)) {
                            ConcurrentHashMap<String, ProjectEventListener> map =
                                    eventListeners.get(eventWorkspace);
                            if(map.containsKey(eventPath)) {
                                ProjectEventListener listener = map.remove(eventPath);
                                projectEventService.removeListener(eventWorkspace, eventPath, listener);
                            }
                        }
                    }
                }
            }
        }
    }

    private class ProjectEventListenerImpl implements ProjectEventListener {

        private final String key;

        private ProjectEventListenerImpl(String wsId, String projectPath) {
            key = wsId + projectPath;
        }

        @Override
        public void onEvent(ProjectEvent event) {
            if (cache.containsKey(key)) {
                JavaProject project = cache.get(key);
                project.getIndexManager().indexAll(project);
            }
        }
    }
}
