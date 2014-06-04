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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.Constants;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/** @author Artem Zatsarynnyy */
@Singleton
public class CodenvyPluginProjectTypeExtension implements ProjectTypeExtension {
    private String baseUrl;

    @Inject
    public CodenvyPluginProjectTypeExtension(@Named("extension-url") String baseUrl, ProjectTypeDescriptionRegistry registry) {
        this.baseUrl = baseUrl;
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType(Constants.CODENVY_PLUGIN_ID, Constants.CODENVY_PLUGIN_NAME, Constants.CODENVY_CATEGORY);
    }

    @Override
    public List<Attribute> getPredefinedAttributes() {
        final List<Attribute> list = new ArrayList<>(4);
        list.add(new Attribute(Constants.LANGUAGE, "java"));
        list.add(new Attribute(Constants.FRAMEWORK, "codenvy_sdk"));
        list.add(new Attribute(Constants.BUILDER_NAME, "maven"));
        list.add(new Attribute(Constants.RUNNER_NAME, "sdk"));
        return list;
    }

    @Override
    public List<ProjectTemplateDescription> getTemplates() {
        final List<ProjectTemplateDescription> list = new ArrayList<>(3);
        list.add(new ProjectTemplateDescription("zip",
                                                "GIST EXAMPLE",
                                                "Simple Codenvy extension project is demonstrating basic usage Codenvy API.",
                                                baseUrl + "/gist-extension.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "EMPTY EXTENSION PROJECT",
                                                "This is a ready to use structure of a Codenvy extension with a minimal set of files and dependencies.",
                                                baseUrl + "/empty-extension.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "HELLO WORLD EXTENSION",
                                                "This is a simple Codenvy Extension that prints Hello World in Output console and adds Hello World item to a content menu.",
                                                baseUrl + "/helloworld-extension.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "ANALYTICS EVENT LOGGER EXTENSION",
                                                "This is a simple Codenvy Extension that logs an event for the Analytics.",
                                                baseUrl + "/analytics-event-logger-extension.zip"));
        return list;
    }
}
