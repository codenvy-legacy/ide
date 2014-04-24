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
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEventHandler;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.java.client.editor.JavaEditorProvider;
import com.codenvy.ide.ext.java.client.editor.JavaReconcilerStrategy;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProjectModelProvider;
import com.codenvy.ide.ext.java.client.wizard.NewAnnotationProvider;
import com.codenvy.ide.ext.java.client.wizard.NewClassProvider;
import com.codenvy.ide.ext.java.client.wizard.NewEnumProvider;
import com.codenvy.ide.ext.java.client.wizard.NewInterfaceProvider;
import com.codenvy.ide.ext.java.client.wizard.NewPackageProvider;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilingStrategy;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD_CONTEXT_MENU;

/** @author Evgen Vidolob */
@Extension(title = "Java syntax highlighting and code autocompletion.", version = "3.0.0")
public class JavaExtension {
    private NotificationManager notificationManager;
    private String              restContext;
    private String              workspaceId;
    private AsyncRequestFactory asyncRequestFactory;
    private EditorAgent         editorAgent;

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
                         EditorAgent editorAgent,
                         AnalyticsEventLogger eventLogger) {
        this.notificationManager = notificationManager;
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.asyncRequestFactory = asyncRequestFactory;
        this.editorAgent = editorAgent;

        iconRegistry.registerIcon("jar.projecttype.big.icon", "java-extension/jar_64.png");
//        iconRegistry.registerIcon("jar.projecttype.small.icon", "java-extension/jar.png");
        iconRegistry.registerIcon("jar.folder.small.icon", "java-extension/package-icon.png");
        iconRegistry.registerIcon("jar/java.file.small.icon", "java-extension/java-icon.png");
        iconRegistry.registerIcon("jar/xml.file.small.icon", "java-extension/xml-icon.png");
        iconRegistry.registerIcon("jar/css.file.small.icon", "java-extension/css-icon.png");
        iconRegistry.registerIcon("jar/js.file.small.icon", "java-extension/js-icon.png");
        iconRegistry.registerIcon("jar/json.file.small.icon", "java-extension/json-icon.png");
        iconRegistry.registerIcon("jar/html.file.small.icon", "java-extension/html-icon.png");
        iconRegistry.registerIcon("jar/html.file.small.icon", "java-extension/html-icon.png");
        iconRegistry.registerIcon("jar/jsp.file.small.icon", "java-extension/jsp-icon.png");
        iconRegistry.registerIcon("jar/gif.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("jar/jpg.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("jar/png.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("jar/pom.xml.file.small.icon", "java-extension/maven-icon.png");

        iconRegistry.registerIcon("java.class", "java-extension/java-icon.png");
        iconRegistry.registerIcon("java.package", "java-extension/package-icon.png");

        iconRegistry.registerIcon("spring.projecttype.big.icon", "java-extension/Spring-Logo.png");
//        iconRegistry.registerIcon("spring.projecttype.small.icon", "java-extension/Spring-Logo.png");
        iconRegistry.registerIcon("spring.folder.small.icon", "java-extension/package-icon.png");
        iconRegistry.registerIcon("spring/java.file.small.icon", "java-extension/java-icon.png");
        iconRegistry.registerIcon("spring/xml.file.small.icon", "java-extension/xml-icon.png");
        iconRegistry.registerIcon("spring/html.file.small.icon", "java-extension/html-icon.png");
        iconRegistry.registerIcon("spring/jsp.file.small.icon", "java-extension/jsp-icon.png");
        iconRegistry.registerIcon("spring/css.file.small.icon", "java-extension/css-icon.png");
        iconRegistry.registerIcon("spring/js.file.small.icon", "java-extension/js-icon.png");
        iconRegistry.registerIcon("spring/json.file.small.icon", "java-extension/json-icon.png");
        iconRegistry.registerIcon("spring/gif.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("spring/jpg.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("spring/png.file.small.icon", "java-extension/image-icon.png");//pom.xml
        iconRegistry.registerIcon("spring/pom.xml.file.small.icon", "java-extension/maven-icon.png");

        iconRegistry.registerIcon("java.class", "java-extension/java-icon.png");
        iconRegistry.registerIcon("java.package", "java-extension/package-icon.png");

        iconRegistry.registerIcon("war.projecttype.big.icon", "java-extension/web_app_big.png");
//        iconRegistry.registerIcon("war.projecttype.small.icon", "java-extension/web_app_big.png");
        iconRegistry.registerIcon("war.folder.small.icon", "java-extension/package-icon.png");
        iconRegistry.registerIcon("war/java.file.small.icon", "java-extension/java-icon.png");
        iconRegistry.registerIcon("war/xml.file.small.icon", "java-extension/xml-icon.png");
        iconRegistry.registerIcon("war/html.file.small.icon", "java-extension/html-icon.png");
        iconRegistry.registerIcon("war/jsp.file.small.icon", "java-extension/jsp-icon.png");
        iconRegistry.registerIcon("war/css.file.small.icon", "java-extension/css-icon.png");
        iconRegistry.registerIcon("war/js.file.small.icon", "java-extension/js-icon.png");
        iconRegistry.registerIcon("war/json.file.small.icon", "java-extension/json-icon.png");
        iconRegistry.registerIcon("war/gif.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("war/jpg.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("war/png.file.small.icon", "java-extension/image-icon.png");
        iconRegistry.registerIcon("war/pom.xml.file.small.icon", "java-extension/maven-icon.png");

        iconRegistry.registerIcon("java.class", "java-extension/java-icon.png");
        iconRegistry.registerIcon("java.package", "java-extension/package-icon.png");

        FileType javaFile = new FileType(JavaResources.INSTANCE.java(), MimeType.APPLICATION_JAVA, "java");
        editorRegistry.register(javaFile, javaEditorProvider);
        resourceProvider.registerFileType(javaFile);

        resourceProvider.registerModelProvider("java", new JavaProjectModelProvider(eventBus, asyncRequestFactory, projectServiceClient,
                                                                                    dtoUnmarshallerFactory));

        JavaResources.INSTANCE.css().ensureInjected();

        // add actions in context menu
        DefaultActionGroup buildContextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD_CONTEXT_MENU);
        buildContextMenuGroup.addSeparator();
        UpdateDependencyAction dependencyAction = new UpdateDependencyAction(this, resourceProvider, eventLogger);
        actionManager.registerAction("updateDependency", dependencyAction);
        buildContextMenuGroup.addAction(dependencyAction);

        DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD);
        buildMenuActionGroup.add(dependencyAction);

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
        String url = restContext + "/java-name-environment/" + workspaceId + "/update-dependencies?projectpath=" + projectPath;

        final Notification notification = new Notification("Updating dependencies...", PROGRESS);
        notificationManager.showNotification(notification);

        asyncRequestFactory.createGetRequest(url, true).send(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                notification.setMessage("Dependencies successfully updated ");
                notification.setStatus(FINISHED);
                editorAgent.getOpenedEditors().iterate(new StringMap.IterationCallback<EditorPartPresenter>() {
                    @Override
                    public void onIteration(String s, EditorPartPresenter editorPartPresenter) {
                        if (editorPartPresenter instanceof CodenvyTextEditor) {
                            CodenvyTextEditor editor = (CodenvyTextEditor)editorPartPresenter;
                            Reconciler reconciler = editor.getConfiguration().getReconciler(editor.getView());
                            if (reconciler != null) {
                                ReconcilingStrategy strategy = reconciler.getReconcilingStrategy(Document.DEFAULT_CONTENT_TYPE);
                                if (strategy != null && strategy instanceof JavaReconcilerStrategy) {
                                    ((JavaReconcilerStrategy)strategy).parse();
                                }
                            }
                        }
                    }
                });
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
