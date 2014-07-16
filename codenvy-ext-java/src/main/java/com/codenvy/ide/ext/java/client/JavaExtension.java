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
package com.codenvy.ide.ext.java.client;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEventHandler;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.java.client.action.NewJavaClassAction;
import com.codenvy.ide.ext.java.client.action.NewPackageAction;
import com.codenvy.ide.ext.java.client.action.UpdateDependencyAction;
import com.codenvy.ide.ext.java.client.editor.JavaEditorProvider;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorker;
import com.codenvy.ide.ext.java.client.editor.JavaReconcilerStrategy;
import com.codenvy.ide.ext.java.client.format.FormatController;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProjectModelProvider;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilingStrategy;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_BUILD_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_FILE_NEW;

/** @author Evgen Vidolob */
@Extension(title = "Java syntax highlighting and code autocompletion.", version = "3.0.0")
public class JavaExtension {
    boolean updating      = false;
    boolean needForUpdate = false;
    private NotificationManager      notificationManager;
    private String                   restContext;
    private String                   workspaceId;
    private AsyncRequestFactory      asyncRequestFactory;
    private EditorAgent              editorAgent;
    private JavaLocalizationConstant localizationConstant;
    private JavaParserWorker parserWorker;
    private BuildContext buildContext;

    @Inject
    public JavaExtension(ResourceProvider resourceProvider,
                         FileTypeRegistry fileTypeRegistry,
                         NotificationManager notificationManager,
                         EditorRegistry editorRegistry,
                         JavaEditorProvider javaEditorProvider,
                         EventBus eventBus,
                         @Named("restContext") String restContext,
                         @Named("workspaceId") String workspaceId,
                         ActionManager actionManager,
                         AsyncRequestFactory asyncRequestFactory,
                         ProjectServiceClient projectServiceClient,
                         IconRegistry iconRegistry,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         EditorAgent editorAgent,
                         AnalyticsEventLogger eventLogger,
                         JavaResources resources,
                         JavaLocalizationConstant localizationConstant,
                         NewPackageAction newPackageAction,
                         NewJavaClassAction newJavaClassAction,
                         JavaParserWorker parserWorker,
                         @Named("JavaFileType") FileType javaFile,
                         /** Create an instance of the FormatController is used for the correct operation of the formatter. Do not
                          * delete!. */
                         FormatController formatController, BuildContext buildContext) {
        this.notificationManager = notificationManager;
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.asyncRequestFactory = asyncRequestFactory;
        this.editorAgent = editorAgent;
        this.localizationConstant = localizationConstant;
        this.parserWorker = parserWorker;
        this.buildContext = buildContext;

        iconRegistry.registerIcon(new Icon("java.class", "java-extension/java-icon.png"));
        iconRegistry.registerIcon(new Icon("java.package", "java-extension/package-icon.png"));
        iconRegistry.registerIcon(new Icon("maven.projecttype.big.icon", "java-extension/jar_64.png"));
        iconRegistry.registerIcon(new Icon("maven.folder.small.icon", resources.packageIcon()));
        iconRegistry.registerIcon(new Icon("maven/java.file.small.icon", resources.javaFile()));
        iconRegistry.registerIcon(new Icon("maven/xml.file.small.icon", resources.xmlFile()));
        iconRegistry.registerIcon(new Icon("maven/css.file.small.icon", resources.cssFile()));
        iconRegistry.registerIcon(new Icon("maven/js.file.small.icon", resources.jsFile()));
        iconRegistry.registerIcon(new Icon("maven/json.file.small.icon", resources.jsonFile()));
        iconRegistry.registerIcon(new Icon("maven/html.file.small.icon", resources.htmlFile()));
        iconRegistry.registerIcon(new Icon("maven/jsp.file.small.icon", resources.jspFile()));
        iconRegistry.registerIcon(new Icon("maven/gif.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon("maven/jpg.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon("maven/png.file.small.icon", resources.imageIcon()));
        iconRegistry.registerIcon(new Icon("maven/pom.xml.file.small.icon", resources.maven()));

        editorRegistry.registerDefaultEditor(javaFile, javaEditorProvider);
        fileTypeRegistry.registerFileType(javaFile);

        resourceProvider.registerModelProvider("java", new JavaProjectModelProvider(eventBus, asyncRequestFactory, projectServiceClient,
                                                                                    dtoUnmarshallerFactory));

        JavaResources.INSTANCE.css().ensureInjected();

        // add actions to New group
        actionManager.registerAction(localizationConstant.actionNewPackageId(), newPackageAction);
        actionManager.registerAction(localizationConstant.actionNewClassId(), newJavaClassAction);
        DefaultActionGroup newGroup = (DefaultActionGroup)actionManager.getAction(GROUP_FILE_NEW);
        newGroup.addSeparator();
        newGroup.add(newJavaClassAction);
        newGroup.add(newPackageAction);

        // add actions in context menu
        DefaultActionGroup buildContextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD_CONTEXT_MENU);
        buildContextMenuGroup.addSeparator();
        UpdateDependencyAction dependencyAction = new UpdateDependencyAction(this, resourceProvider, eventLogger, resources, buildContext);
        actionManager.registerAction("updateDependency", dependencyAction);
        buildContextMenuGroup.addAction(dependencyAction);

        DefaultActionGroup buildMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_BUILD);
        buildMenuActionGroup.add(dependencyAction);

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

    public static native String getJavaCAPath() /*-{
        try {
            return $wnd.IDE.config.javaCodeAssistant;
        } catch (e) {
            return null;
        }

    }-*/;

    public void updateDependencies(final Project project) {
        if (updating) {
            needForUpdate = true;
            return;
        }
        String projectPath = project.getPath();
        String url = getJavaCAPath() + "/java-name-environment/" + workspaceId + "/update-dependencies?projectpath=" + projectPath;

        final Notification notification = new Notification(localizationConstant.updatingDependencies(), PROGRESS);
        notificationManager.showNotification(notification);
        buildContext.setBuilding(true);
        updating = true;
        asyncRequestFactory.createGetRequest(url, true).send(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                updating = false;
                notification.setMessage(localizationConstant.dependenciesSuccessfullyUpdated());
                notification.setStatus(FINISHED);
                buildContext.setBuilding(false);
                parserWorker.dependenciesUpdated();
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
                        if (needForUpdate) {
                            needForUpdate = false;
                            updateDependencies(project);
                        }
                    }
                });
            }

            @Override
            protected void onFailure(Throwable exception) {
                updating = false;
                needForUpdate = false;
                notification.setMessage(exception.getMessage());
                notification.setType(ERROR);
                notification.setStatus(FINISHED);
                buildContext.setBuilding(false);
            }
        });
    }
}
