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
        list.add(new ProjectTemplateDescription("git",
                                                "Java Web App",
                                                "Simple JSP project",
                                                "https://github.com/codenvy-templates/web-jsp-java-basic.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Java Spring Application",
                                                "Simple Spring project that uses Maven build system",
                                                "https://github.com/codenvy-templates/web-spring-java-simple.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Maven Jar Project",
                                                "Simple Java library that uses Maven build system",
                                                "https://github.com/codenvy-templates/desktop-jar-java.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Java Console App",
                                                "Simple Java application that writes 'Hello World!' in a console",
                                                "https://github.com/codenvy-templates/desktop-console-java.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Swing GUI App",
                                                "Simple GUI application built with Swing components",
                                                "https://github.com/codenvy-templates/desktop-swing-java-basic.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "GWT Web App",
                                                "Simple GWT Application",
                                                "https://github.com/codenvy-templates/web-gwt-java-simple.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Play 1 Web App",
                                                "Simple Application that uses Play v1 framework",
                                                "https://github.com/codenvy-templates/web-play1-java-simple"));
        list.add(new ProjectTemplateDescription("git",
                                                "Play 2 Web App",
                                                "Simple Application that uses Play v2 framework",
                                                "https://github.com/codenvy-templates/web-play2-java-simple"));
        list.add(new ProjectTemplateDescription("git",
                                                "Grails App",
                                                "Simple Grails application",
                                                "https://github.com/codenvy-templates/web-grails-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "JavaServer Faces App",
                                                "Simple JavaServer Faces application",
                                                "https://github.com/codenvy-templates/web-jsf-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Lift App",
                                                "Simple application that uses Lift framework",
                                                "https://github.com/codenvy-templates/web-lift-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Struts App",
                                                "Simple application that uses Apache Struts framework",
                                                "https://github.com/codenvy-templates/web-struts-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Vaadin App",
                                                "Simple Vaadin application",
                                                "https://github.com/codenvy-templates/web-vaadin-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Android App",
                                                "Simple Android Application built with Maven and run natively in Android Emulator",
                                                "https://github.com/codenvy-templates/mobile-android-java-basic.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Tapestry App",
                                                "Simple application that uses Apache Tapestry web framework",
                                                "https://github.com/codenvy-templates/web-tapestry-java-simple.git"));

        list.add(new ProjectTemplateDescription("git",
                                                "Wicket App",
                                                "Simple application that uses Apache Wicket web framework",
                                                "https://github.com/codenvy-templates/web-wicket-java-simple.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Groovy App",
                                                "Simple Groovy web application",
                                                "https://github.com/codenvy-templates/web-groovy-java-simple.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Clojure App",
                                                "Sample app using Clojure framework",
                                                "https://github.com/codenvy-templates/desktop-clojure-java-simple.git"));
        list.add(new ProjectTemplateDescription("git",
                                                "Scala App",
                                                "Simple app using Scala object-oriented language",
                                                "https://github.com/codenvy-templates/desktop-scala-java-simple.git"));

        return list;
    }
}