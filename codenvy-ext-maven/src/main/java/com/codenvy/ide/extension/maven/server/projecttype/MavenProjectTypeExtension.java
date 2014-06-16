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
                                                "Java Web App",
                                                "Simple JSP project",
                                                "templates/MavenWar.zip"));
        list.add(new ProjectTemplateDescription("git",
                                                "Java Spring Application",
                                                "Simple Spring project that uses Maven build system",
                                                "https://github.com/codenvy-templates/web-spring-java-simple.git"));
        list.add(new ProjectTemplateDescription("zip",
                                                "Maven Jar Project",
                                                "Simple JAR project that uses Maven build system",
                                                "templates/MavenJar.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "Java Console App",
                                                "Simple Java application that writes 'Hello World!' in a console",
                                                "templates/MavenConsoleApp.zip"));
        list.add(new ProjectTemplateDescription("git",
                                                "Swing GUI App",
                                                "Simple GUI Application that uses Swing components",
                                                "https://github.com/codenvy-templates/desktop-swing-java-basic.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "GWT Web App",
                                                "Simple GWT Application",
                                                "https://github.com/codenvy-templates/web-gwt-java-simple.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Play 1 Web App",
                                                "Simple Application that uses Play v1 Framework",
                                                "https://github.com/codenvy-templates/web-play1-java-simple"));
        list.add(new ProjectTemplateDescription("git",
                                                "Play 2 Web App",
                                                "Simple Application that uses Play v2 Framework",
                                                "https://github.com/codenvy-templates/web-play2-java-simple"));
        list.add(new ProjectTemplateDescription("git",
                                                "Grails App",
                                                "Simple Grails Application",
                                                "https://github.com/codenvy-templates/web-grails-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "JSF App",
                                                "Simple JavaServer Faces Application",
                                                "https://github.com/codenvy-templates/web-jsf-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Lift App",
                                                "Simple application that uses Lift framework",
                                                "https://github.com/codenvy-templates/web-lift-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "MAVEN Struts App",
                                                "Simple Struts Application",
                                                "https://github.com/codenvy-templates/web-struts-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Vaadin App",
                                                "Simple Vaadin Application",
                                                "https://github.com/codenvy-templates/web-vaadin-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Android App",
                                                "Simple Android Application built with Maven",
                                                "https://github.com/codenvy-templates/mobile-android-java-basic.git"));

        return list;
    }
}