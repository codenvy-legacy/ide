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
package com.codenvy.ide.extension.ant.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.java.client.JavaClientBundle;
import com.codenvy.ide.extension.ant.client.template.CreateJavaProjectPage;
import com.codenvy.ide.extension.ant.client.template.CreateSpringProjectPage;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.java.client.JavaExtension.JAVA_APPLICATION_PROJECT_TYPE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.json.JsonCollections.createArray;


/**
 *
 */
@Singleton
@Extension(title = "Ant Support.")
public class AntExtension {

    /** Channel for the messages containing status of the Maven build job. */
    public static final String ANT_SPRING_APPLICATION_PROJECT_TYPE = "Ant_Spring";
    public static final String ANT_SPRING_PROJECT_ID               = "Ant_Spring";
    public static final String ANT_JAR_PROJECT_ID                  = "Ant_Jar";

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
    public AntExtension(TemplateAgent templateAgent,
                        ProjectTypeAgent projectTypeAgent,
                        Provider<CreateJavaProjectPage> createJavaProjectPage,
                        Provider<CreateSpringProjectPage> createSpringProjectPage,
                        ActionManager actionManager) {
        // register actions

        templateAgent.register(ANT_JAR_PROJECT_ID,
                               "Ant Java project",
                               JavaClientBundle.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(JAVA_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createJavaProjectPage));
        templateAgent.register(ANT_SPRING_PROJECT_ID,
                               "Ant Spring project",
                               JavaClientBundle.INSTANCE.javaProject(),
                               PRIMARY_NATURE,
                               createArray(ANT_SPRING_APPLICATION_PROJECT_TYPE),
                               JsonCollections.<Provider<? extends AbstractTemplatePage>>createArray(createSpringProjectPage));

        projectTypeAgent.register(ANT_SPRING_APPLICATION_PROJECT_TYPE,
                                  "Ant Spring application",
                                  JavaClientBundle.INSTANCE.newJavaProject(),
                                  PRIMARY_NATURE,
                                  createArray(ANT_SPRING_APPLICATION_PROJECT_TYPE));
    }
}