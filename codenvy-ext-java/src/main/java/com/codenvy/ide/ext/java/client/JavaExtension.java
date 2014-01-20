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
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.java.client.editor.JavaEditorProvider;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProjectModelProvider;
import com.codenvy.ide.ext.java.client.projecttemplate.ant.CreateAntJavaProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.ant.CreateAntSpringProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenJavaProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenSpringProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenWarProjectPage;
import com.codenvy.ide.ext.java.client.wizard.NewAnnotationProvider;
import com.codenvy.ide.ext.java.client.wizard.NewClassProvider;
import com.codenvy.ide.ext.java.client.wizard.NewEnumProvider;
import com.codenvy.ide.ext.java.client.wizard.NewInterfaceProvider;
import com.codenvy.ide.ext.java.client.wizard.NewPackageProvider;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_PROJECT;
import static com.codenvy.ide.collections.Collections.createArray;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;

/** @author Evgen Vidolob */
@Extension(title = "Java syntax highlighting and autocomplete.", version = "3.0.0")
public class JavaExtension {
    private static final String JAVA_PERSPECTIVE                  = "Java";
    public static final  String JAVA_APPLICATION_PROJECT_TYPE     = "Jar";
    public static final  String JAVA_WEB_APPLICATION_PROJECT_TYPE = "War";

    public static final String PROJECT_BUILD_GROUP_MAIN_MENU   = "ProjectBuildGroup";
    /** Channel for the messages containing status of the Maven build job. */
    public static final String BUILD_STATUS_CHANNEL            = "builder:buildStatus:";
    public static final String SPRING_APPLICATION_PROJECT_TYPE = "Spring";
    public static final String WAR_PROJECT_ID                  = "War";
    public static final String SPRING_PROJECT_ID               = "Spring";
    public static final String JAR_PROJECT_ID                  = "Jar";
    public static final String ANT_SPRING_PROJECT_ID           = "Ant_Spring";
    public static final String ANT_JAR_PROJECT_ID              = "Ant_Jar";

    private ResourceProvider    resourceProvider;
    private NotificationManager notificationManager;
    private String              restContext;

    /**
     *
     */
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
                         ProjectTypeAgent projectTypeAgent,
                         @Named("restContext") String restContext,
                         TemplateAgent templateAgent,
                         Provider<CreateMavenJavaProjectPage> createMavenJavaProjectPage,
                         Provider<CreateMavenWarProjectPage> createMavenWarProjectPage,
                         Provider<CreateMavenSpringProjectPage> createMavenSpringProjectPage,
                         Provider<CreateAntJavaProjectPage> createAntJavaProjectPage,
                         Provider<CreateAntSpringProjectPage> createAntSpringProjectPage,ActionManager actionManager) {

        this();
        FileType javaFile = new FileType(JavaResources.INSTANCE.java(), MimeType.APPLICATION_JAVA, "java");
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.restContext = restContext;

        editorRegistry.register(javaFile, javaEditorProvider);
        resourceProvider.registerFileType(javaFile);
        resourceProvider.registerModelProvider(JavaProject.PRIMARY_NATURE, new JavaProjectModelProvider(eventBus));
        JavaResources.INSTANCE.css().ensureInjected();

        // add actions in context menu
        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);
        contextMenuGroup.addSeparator();
        UpdateDependencyAction dependencyAction = new UpdateDependencyAction(this, resourceProvider);
        actionManager.registerAction("updateDependency", dependencyAction);
        contextMenuGroup.addAction(dependencyAction);

        DefaultActionGroup projectMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_PROJECT);
        projectMenuActionGroup.add(dependencyAction);

        Array<String> emptyArray = Collections.createArray();

        // Jar project properties
        Array<Property> jarProperties = Collections.createArray();
        jarProperties.add(new Property("nature.primary", Collections.createArray("java")));
        jarProperties.add(new Property("vfs:projectType", Collections.createArray("Jar")));
        jarProperties.add(new Property("exoide:classpath", emptyArray));
        jarProperties.add(new Property("nature.mixin", Collections.createArray("Jar")));
        jarProperties.add(new Property("vfs:mimeType", Collections.createArray("text/vnd.ideproject+directory")));
        jarProperties.add(new Property("builder.name", Collections.createArray("maven")));
        jarProperties.add(new Property("folders.source", Collections.createArray("src/main/java", "src/test/java")));
        jarProperties.add(new Property("exoide:projectDescription", Collections.createArray("Simple JAR project.")));

        // War project properties
        Array<Property> warProperties = Collections.createArray();
        warProperties.add(new Property("nature.primary", Collections.createArray("java")));
        warProperties.add(new Property("exoide:classpath", emptyArray));
        warProperties.add(new Property("nature.mixin", Collections.createArray("War")));
        warProperties.add(new Property("exoide:target", Collections.createArray("CloudBees", "CloudFoundry", "AWS", "AppFog", "Tier3WF")));
        warProperties.add(new Property("runner.name", Collections.createArray("webapps")));
        warProperties.add(new Property("exoide:projectDescription", Collections.createArray("Java Web project.")));
        warProperties.add(new Property("vfs:projectType", Collections.createArray("War")));
        warProperties.add(new Property("vfs:mimeType", Collections.createArray("text/vnd.ideproject+directory")));
        warProperties.add(new Property("builder.name", Collections.createArray("maven")));
        warProperties.add(new Property("folders.source", Collections.createArray("src/main/java", "src/main/resources")));

        projectTypeAgent.register(JAVA_APPLICATION_PROJECT_TYPE,
                                  "Java application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(JAVA_APPLICATION_PROJECT_TYPE),
                                  jarProperties);

        projectTypeAgent.register(JAVA_WEB_APPLICATION_PROJECT_TYPE,
                                  "Java web application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(JAVA_WEB_APPLICATION_PROJECT_TYPE),
                                  warProperties);

        projectTypeAgent.register(SPRING_APPLICATION_PROJECT_TYPE,
                                  "Spring application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(SPRING_APPLICATION_PROJECT_TYPE),
                                  warProperties);

        newResourceAgent.register(newClassHandler);
        newResourceAgent.register(newInterfaceHandler);
        newResourceAgent.register(newEnumHandler);
        newResourceAgent.register(newAnnotationHandler);
        newResourceAgent.register(newPackage);

        templateAgent.register(WAR_PROJECT_ID,
                               "War project",
                               null,
                               PRIMARY_NATURE,
                               createArray(JAVA_WEB_APPLICATION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createMavenWarProjectPage));
        templateAgent.register(JAR_PROJECT_ID,
                               "Java project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(JAVA_APPLICATION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createMavenJavaProjectPage));
        templateAgent.register(SPRING_PROJECT_ID,
                               "Spring project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(SPRING_APPLICATION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createMavenSpringProjectPage));

        templateAgent.register(ANT_JAR_PROJECT_ID,
                               "Ant Java project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(JAVA_APPLICATION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createAntJavaProjectPage));
        templateAgent.register(ANT_SPRING_PROJECT_ID,
                               "Ant Spring project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(SPRING_APPLICATION_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createAntSpringProjectPage));


        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                Project project = event.getProject();
                if (project instanceof JavaProject)
                    updateDependencies();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                // do nothing
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
                // do nothing
            }
        });
        eventBus.addHandler(FileEvent.TYPE, new FileEventHandler() {
            @Override
            public void onFileOperation(FileEvent event) {
                String name = event.getFile().getName();
                if (event.getOperationType() == FileEvent.FileOperation.SAVE && "pom.xml".equals(name))
                    updateDependencies();
            }
        });
    }

    /** For test use only. */
    public JavaExtension() {
    }

    public void updateDependencies() {
        Project project = resourceProvider.getActiveProject();
        String projectId = project.getId();
        String vfsId = resourceProvider.getVfsInfo().getId();
        String url = restContext + "/code-assistant-java/" + Utils.getWorkspaceName() + "/update-dependencies?projectid=" + projectId +
                     "&vfsid=" + vfsId;

        final Notification notification = new Notification("Updating dependencies...", PROGRESS);
        notificationManager.showNotification(notification);

        StringUnmarshaller unmarshaller = new StringUnmarshaller();
        try {
            AsyncRequest.build(RequestBuilder.GET, url, true).send(new AsyncRequestCallback<String>(unmarshaller) {
                @Override
                protected void onSuccess(String result) {
                    notification.setMessage("Dependencies successfully updated ");
                    notification.setStatus(FINISHED);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    JSONObject object = JSONParser.parseLenient(exception.getMessage()).isObject();
                    if (object.containsKey("message"))
                        notification.setMessage(object.get("message").isString().stringValue());
                    else
                        notification.setMessage("Update dependencies fail");
                    notification.setType(ERROR);
                    notification.setStatus(FINISHED);
                }
            });
        } catch (RequestException e) {
            notification.setMessage(e.getMessage());
            notification.setType(ERROR);
            notification.setStatus(FINISHED);
        }
    }

//    private boolean hasPomFile(@NotNull Array<Resource> children) {
//        for (Resource child : children.asIterable()) {
//            if (child instanceof File && "pom.xml".equals(child.getName())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
