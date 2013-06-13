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