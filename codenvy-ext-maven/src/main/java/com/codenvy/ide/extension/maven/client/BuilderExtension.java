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
package com.codenvy.ide.extension.maven.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.extension.maven.client.actions.BuildAction;
import com.codenvy.ide.extension.maven.client.actions.BuildAndPublishAction;
import com.codenvy.ide.extension.maven.client.build.BuildProjectPresenter;
import com.codenvy.ide.extension.maven.client.template.CreateJavaProjectPage;
import com.codenvy.ide.extension.maven.client.template.CreateSpringProjectPage;
import com.codenvy.ide.extension.maven.client.template.CreateWarProjectPage;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_PROJECT;
import static com.codenvy.ide.ext.java.client.JavaExtension.JAVA_APPLICATION_PROJECT_TYPE;
import static com.codenvy.ide.ext.java.client.JavaExtension.JAVA_WEB_APPLICATION_PROJECT_TYPE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.json.JsonCollections.createArray;

/**
 * Maven builder extension entry point.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderExtension.java Feb 21, 2012 1:53:48 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Maven Support.", version = "3.0.0")
public class BuilderExtension {
    public static final String PROJECT_BUILD_GROUP_MAIN_MENU   = "ProjectBuildGroup";
    /** Channel for the messages containing status of the Maven build job. */
    public static final String BUILD_STATUS_CHANNEL            = "maven:buildStatus:";
    public static final String SPRING_APPLICATION_PROJECT_TYPE = "Spring";
    public static final String WAR_PROJECT_ID                  = "War";
    public static final String SPRING_PROJECT_ID               = "Spring";
    public static final String JAR_PROJECT_ID                  = "Jar";

    /**
     * Create extension.
     *
     * @param buildProjectPresenter
     * @param templateAgent
     * @param projectTypeAgent
     * @param createJavaProjectPage
     * @param createWarProjectPage
     * @param createSpringProjectPage
     */
    @Inject
    public BuilderExtension(BuildProjectPresenter buildProjectPresenter,
                            TemplateAgent templateAgent,
                            ProjectTypeAgent projectTypeAgent,
                            Provider<CreateJavaProjectPage> createJavaProjectPage,
                            Provider<CreateWarProjectPage> createWarProjectPage,
                            Provider<CreateSpringProjectPage> createSpringProjectPage,
                            BuilderLocalizationConstant localizationConstants,
                            ActionManager actionManager,
                            BuildAction buildAction,
                            BuildAndPublishAction buildAndPublishAction) {
        // register actions
        actionManager.registerAction(localizationConstants.buildProjectControlId(), buildAction);
        actionManager.registerAction(localizationConstants.buildAndPublishProjectControlId(), buildAndPublishAction);

        // compose action group
        DefaultActionGroup buildGroup = new DefaultActionGroup(PROJECT_BUILD_GROUP_MAIN_MENU, false, actionManager);
        buildGroup.add(buildAction);
        buildGroup.add(buildAndPublishAction);

        // add action group to 'Project' menu
        DefaultActionGroup projectMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_PROJECT);
        projectMenuActionGroup.addSeparator();
        projectMenuActionGroup.add(buildGroup);

        templateAgent.register(WAR_PROJECT_ID,
                               "War project",
                               null,
                               PRIMARY_NATURE,
                               createArray(JAVA_WEB_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createWarProjectPage));
        templateAgent.register(JAR_PROJECT_ID,
                               "Java project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(JAVA_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createJavaProjectPage));
        templateAgent.register(SPRING_PROJECT_ID,
                               "Spring project",
                               JavaResources.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(SPRING_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createSpringProjectPage));

        projectTypeAgent.register(SPRING_APPLICATION_PROJECT_TYPE,
                                  "Spring application",
                                  JavaResources.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(SPRING_APPLICATION_PROJECT_TYPE));
    }
}