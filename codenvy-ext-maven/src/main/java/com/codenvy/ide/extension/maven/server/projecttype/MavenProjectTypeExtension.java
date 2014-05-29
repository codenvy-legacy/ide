/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.extension.maven.server.projecttype;

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.java.shared.Constants;
import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class MavenProjectTypeExtension implements ProjectTypeExtension {

    @Inject
    public MavenProjectTypeExtension(ProjectTypeDescriptionRegistry registry) {
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType(Constants.MAVEN_ID, Constants.MAVEN_NAME, Constants.JAVA_CATEGORY);
    }

    @Override
    public List<Attribute> getPredefinedAttributes() {
        final List<Attribute> list = new ArrayList<>(2);
        list.add(new Attribute(Constants.LANGUAGE, "java"));
        list.add(new Attribute(Constants.BUILDER_NAME, "maven"));
        return list;
    }

    @Override
    public List<ProjectTemplateDescription> getTemplates() {
        final List<ProjectTemplateDescription> list = new ArrayList<>(1);
        list.add(new ProjectTemplateDescription("zip",
                                                "JAVA WEB PROJECT",
                                                "Java Web project.",
                                                "templates/MavenWar.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "MAVEN SPRING APPLICATION",
                                                "Simple Spring project which uses Maven build system.",
                                                "templates/MavenSpring.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "MAVEN JAR PROJECT",
                                                "Simple JAR project which uses Maven build system.",
                                                "templates/MavenJar.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "MAVEN Console App",
                                                "Simple Console Application.",
                                                "templates/MavenConsoleApp.zip"));
        return list;
    }
}
