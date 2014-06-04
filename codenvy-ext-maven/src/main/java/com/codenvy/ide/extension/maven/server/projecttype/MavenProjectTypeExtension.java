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
        final List<ProjectTemplateDescription> list = new ArrayList<>(5);
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
        list.add(new ProjectTemplateDescription("zip",
                                                "MAVEN GUI App",
                                                "Simple GUI Application.",
                                                "templates/MavenGUIApp.zip"));
        return list;
    }
}
