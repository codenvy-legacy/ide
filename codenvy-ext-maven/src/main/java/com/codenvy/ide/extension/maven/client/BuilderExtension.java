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
import com.codenvy.ide.ext.java.client.JavaClientBundle;
import com.codenvy.ide.extension.maven.client.build.BuildProjectPresenter;
import com.codenvy.ide.extension.maven.client.template.CreateJavaProjectPresenter;
import com.codenvy.ide.extension.maven.client.template.CreateSpringProjectPresenter;
import com.codenvy.ide.extension.maven.client.template.CreateWarProjectPresenter;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    /** Channel for the messages containing status of the Maven build job. */
    public static final String BUILD_STATUS_CHANNEL            = "maven:buildStatus:";
    public static final String SPRING_APPLICATION_PROJECT_TYPE = "Spring";

    /**
     * Create extension.
     *
     * @param buildProjectPresenter
     * @param templateAgent
     * @param createProjectPresenter
     * @param createJavaProjectPresenter
     * @param createSpringProjectPresenter
     * @param projectTypeAgent
     */
    @Inject
    public BuilderExtension(BuildProjectPresenter buildProjectPresenter, TemplateAgent templateAgent,
                            CreateWarProjectPresenter createProjectPresenter, CreateJavaProjectPresenter createJavaProjectPresenter,
                            CreateSpringProjectPresenter createSpringProjectPresenter, ProjectTypeAgent projectTypeAgent) {
        templateAgent.registerTemplate("War project", null, createArray(JAVA_WEB_APPLICATION_PROJECT_TYPE),
                                       createProjectPresenter, null);
        templateAgent.registerTemplate("Java project", JavaClientBundle.INSTANCE.javaProject(),
                                       createArray(PRIMARY_NATURE),
                                       createJavaProjectPresenter, null);
        templateAgent.registerTemplate("Spring project", JavaClientBundle.INSTANCE.javaProject(),
                                       createArray(SPRING_APPLICATION_PROJECT_TYPE),
                                       createSpringProjectPresenter, null);

        projectTypeAgent
                .registerProjectType(SPRING_APPLICATION_PROJECT_TYPE, "Spring application", JavaClientBundle.INSTANCE.newJavaProject());
    }
}