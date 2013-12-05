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
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceAgent;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.java.client.editor.JavaEditorProvider;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProjectModelProvider;
import com.codenvy.ide.ext.java.client.projecttemplate.ant.CreateAntJavaProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.ant.CreateAntSpringProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenJavaProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenSpringProjectPage;
import com.codenvy.ide.ext.java.client.projecttemplate.maven.CreateMavenWarProjectPage;
import com.codenvy.ide.ext.java.client.templates.*;
import com.codenvy.ide.ext.java.client.wizard.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.json.JsonCollections.createArray;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Extension(title = "Java Support : syntax highlighting and autocomplete.", version = "3.0.0")
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

    private ResourceProvider        resourceProvider;
    private NotificationManager     notificationManager;
    private String                  restContext;

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
                         Provider<CreateAntSpringProjectPage> createAntSpringProjectPage) {

        this();
        FileType javaFile = new FileType(JavaResources.INSTANCE.java(), MimeType.APPLICATION_JAVA, "java");
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.restContext = restContext;

        editorRegistry.register(javaFile, javaEditorProvider);
        resourceProvider.registerFileType(javaFile);
        resourceProvider.registerModelProvider(JavaProject.PRIMARY_NATURE, new JavaProjectModelProvider(eventBus));
        JavaResources.INSTANCE.css().ensureInjected();

        projectTypeAgent.register(JavaProject.PRIMARY_NATURE,
                                  "Java application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(JAVA_APPLICATION_PROJECT_TYPE));

        projectTypeAgent.register(JAVA_WEB_APPLICATION_PROJECT_TYPE,
                                  "Java web application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(JAVA_WEB_APPLICATION_PROJECT_TYPE));

        projectTypeAgent.register(SPRING_APPLICATION_PROJECT_TYPE,
                                  "Spring application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(SPRING_APPLICATION_PROJECT_TYPE));

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
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createMavenWarProjectPage));
        templateAgent.register(JAR_PROJECT_ID,
                               "Java project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(JAVA_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createMavenJavaProjectPage));
        templateAgent.register(SPRING_PROJECT_ID,
                               "Spring project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(SPRING_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createMavenSpringProjectPage));

        templateAgent.register(ANT_JAR_PROJECT_ID,
                               "Ant Java project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(JAVA_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createAntJavaProjectPage));
        templateAgent.register(ANT_SPRING_PROJECT_ID,
                               "Ant Spring project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(SPRING_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createAntSpringProjectPage));


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
        JsonArray<Resource> children = project.getChildren();
        if (!children.isEmpty() && hasPomFile(children)) {
            String projectId = project.getId();
            String vfsId = resourceProvider.getVfsId();
            String url = restContext + '/' + Utils.getWorkspaceName() + "/code-assistant/java/update-dependencies?projectid=" + projectId +
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
                        notification.setMessage(exception.getMessage());
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
    }

    private boolean hasPomFile(@NotNull JsonArray<Resource> children) {
        for (Resource child : children.asIterable()) {
            if (child instanceof File && "pom.xml".equals(child.getName())) {
                return true;
            }
        }
        return false;
    }
}
