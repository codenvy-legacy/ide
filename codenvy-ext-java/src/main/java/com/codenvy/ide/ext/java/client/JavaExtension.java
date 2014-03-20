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
package com.codenvy.ide.ext.java.client;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEventHandler;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.ext.java.client.editor.JavaEditorProvider;
import com.codenvy.ide.ext.java.client.format.FormatController;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProjectModelProvider;
import com.codenvy.ide.ext.java.client.wizard.NewAnnotationProvider;
import com.codenvy.ide.ext.java.client.wizard.NewClassProvider;
import com.codenvy.ide.ext.java.client.wizard.NewEnumProvider;
import com.codenvy.ide.ext.java.client.wizard.NewInterfaceProvider;
import com.codenvy.ide.ext.java.client.wizard.NewPackageProvider;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_PROJECT;

/** @author Evgen Vidolob */
@Extension(title = "Java syntax highlighting and code autocompletion.", version = "3.0.0")
public class JavaExtension {
    private ResourceProvider    resourceProvider;
    private NotificationManager notificationManager;
    private String              restContext;
    private String              workspaceId;
    private AsyncRequestFactory asyncRequestFactory;

    @Inject
    public JavaExtension(ResourceProvider resourceProvider,
                         NotificationManager notificationManager,
                         EditorRegistry editorRegistry,
                         JavaEditorProvider javaEditorProvider,
                         EventBus eventBus,
                         NewResourceAgent newResourceAgent,
                         NewClassProvider newClassHandler,
                         NewInterfaceProvider newInterfaceHandler,
                         NewEnumProvider newEnumHandler,
                         NewAnnotationProvider newAnnotationHandler,
                         NewPackageProvider newPackage,
                         @Named("restContext") String restContext,
                         @Named("workspaceId") String workspaceId,
                         ActionManager actionManager,
                         AsyncRequestFactory asyncRequestFactory,
                         ProjectServiceClient projectServiceClient,
                         IconRegistry iconRegistry,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         FormatController formatController) {
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.asyncRequestFactory = asyncRequestFactory;

        Map<String, String> icons = new HashMap<>();
        icons.put("jar.projecttype.big.icon", "java-extension/jar_64.png");
        icons.put("jar.projecttype.small.icon", "java-extension/jar.png");
        icons.put("jar.folder.small.icon", "java-extension/package.gif");
        icons.put("jar/java.file.small.icon", "java-extension/java-class.png");
        icons.put("java.class", "java-extension/java-class.png");
        icons.put("java.package", "java-extension/package.gif");
        icons.put("spring.projecttype.big.icon", "java-extension/Spring-Logo.png");
        icons.put("spring.projecttype.small.icon", "java-extension/Spring-Logo.png");
        icons.put("spring.folder.small.icon", "java-extension/package.gif");
        icons.put("spring/java.file.small.icon", "java-extension/java-class.png");
        icons.put("java.class", "java-extension/java-class.png");
        icons.put("java.package", "java-extension/package.gif");
        icons.put("war.projecttype.big.icon", "java-extension/web_app_big.png");
        icons.put("war.projecttype.small.icon", "java-extension/web_app_big.png");
        icons.put("war.folder.small.icon", "java-extension/package.gif");
        icons.put("war/java.file.small.icon", "java-extension/java-class.png");
        icons.put("java.class", "java-extension/java-class.png");
        icons.put("java.package", "java-extension/package.gif");


        iconRegistry.registerIcons(icons);


        FileType javaFile = new FileType(JavaResources.INSTANCE.java(), MimeType.APPLICATION_JAVA, "java");
        editorRegistry.register(javaFile, javaEditorProvider);
        resourceProvider.registerFileType(javaFile);

        resourceProvider.registerModelProvider("java", new JavaProjectModelProvider(eventBus, asyncRequestFactory, projectServiceClient,
                                                                                    dtoUnmarshallerFactory));

        JavaResources.INSTANCE.css().ensureInjected();

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        contextMenuGroup.addSeparator();
        UpdateDependencyAction dependencyAction = new UpdateDependencyAction(this, resourceProvider);
        actionManager.registerAction("updateDependency", dependencyAction);
        contextMenuGroup.addAction(dependencyAction);

        DefaultActionGroup projectMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_PROJECT);
        projectMenuActionGroup.add(dependencyAction);

        newResourceAgent.register(newClassHandler);
        newResourceAgent.register(newInterfaceHandler);
        newResourceAgent.register(newEnumHandler);
        newResourceAgent.register(newAnnotationHandler);
        newResourceAgent.register(newPackage);

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                Project project = event.getProject();
                if (project instanceof JavaProject) {
                    updateDependencies(project);
                }
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });

        eventBus.addHandler(FileEvent.TYPE, new FileEventHandler() {
            @Override
            public void onFileOperation(FileEvent event) {
                String name = event.getFile().getName();
                if (event.getOperationType() == FileEvent.FileOperation.SAVE && "pom.xml".equals(name)) {
                    updateDependencies(event.getFile().getProject());
                }
            }
        });
    }

    /** For test use only. */
    public JavaExtension() {
    }

    public void updateDependencies(Project project) {
        String projectPath = project.getPath();
        String vfsId = resourceProvider.getVfsInfo().getId();
        String url = restContext + "/java-name-environment/" + workspaceId + "/update-dependencies?projectpath=" + projectPath;

        final Notification notification = new Notification("Updating dependencies...", PROGRESS);
        notificationManager.showNotification(notification);

        asyncRequestFactory.createGetRequest(url, true).send(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                notification.setMessage("Dependencies successfully updated ");
                notification.setStatus(FINISHED);
            }

            @Override
            protected void onFailure(Throwable exception) {
                JSONObject object = JSONParser.parseLenient(exception.getMessage()).isObject();
                if (object.containsKey("message")) {
                    notification.setMessage(object.get("message").isString().stringValue());
                } else {
                    notification.setMessage("Update dependencies fail");
                }
                notification.setType(ERROR);
                notification.setStatus(FINISHED);
            }
        });
    }
}
