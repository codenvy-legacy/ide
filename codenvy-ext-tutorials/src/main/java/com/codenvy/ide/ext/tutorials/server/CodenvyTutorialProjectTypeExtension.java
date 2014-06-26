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
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.codenvy.ide.server.ProjectTemplateDescriptionLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** @author Artem Zatsarynnyy */
@Singleton
public class CodenvyTutorialProjectTypeExtension implements ProjectTypeExtension {
    private static final Logger LOG = LoggerFactory.getLogger(CodenvyTutorialProjectTypeExtension.class);
    private final ProjectTemplateDescriptionLoader projectTemplateDescriptionLoader;
    private final String                           baseUrl;
    private final ProjectType                      projectType;

    @Inject
    public CodenvyTutorialProjectTypeExtension(@Named("extension-url") String baseUrl,
                                               ProjectTemplateDescriptionLoader projectTemplateDescriptionLoader,
                                               ProjectTypeDescriptionRegistry registry) {
        this.baseUrl = baseUrl;
        this.projectTemplateDescriptionLoader = projectTemplateDescriptionLoader;
        projectType = new ProjectType(Constants.TUTORIAL_ID, Constants.TUTORIAL_NAME, Constants.CODENVY_TUTORIAL_CATEGORY);
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return projectType;
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
        final List<ProjectTemplateDescription> list = new ArrayList<>();

        try {
            projectTemplateDescriptionLoader.load(getProjectType().getId(), list);
        } catch (IOException e) {
            LOG.error("Unable to load external templates for project type: {}", getProjectType().getId());
        }

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "NOTIFICATION API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Notification API.",
                                                baseUrl + "/notification-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "ACTION API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Action API.",
                                                baseUrl + "/action-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "WIZARD API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Wizard API.",
                                                baseUrl + "/wizard-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "PART API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Part API.",
                                                baseUrl + "/parts-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "EDITOR API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Editor API.",
                                                baseUrl + "/editor-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "WYSIWYG EDITOR TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to implement " +
                                                "WYSIWYG editor.",
                                                baseUrl + "/wysiwyg-editor-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "GIN TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use GIN with" +
                                                " Codenvy API.",
                                                baseUrl + "/gin-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "THEME API TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use Theme " +
                                                "with Codenvy API.",
                                                baseUrl + "/theme-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "ICON REGISTRY API TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use " +
                                                "IconRegistry.",
                                                baseUrl + "/icons-registry-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("Samples",
                                                "zip",
                                                "SERVER SIDE API TUTORIAL",
                                                "The following tutorial will take you through a simple example demonstrating registration" +
                                                "of a server side component and server-client side communication.",
                                                baseUrl + "/server-side-tutorial.zip"));
        return list;
    }
}
