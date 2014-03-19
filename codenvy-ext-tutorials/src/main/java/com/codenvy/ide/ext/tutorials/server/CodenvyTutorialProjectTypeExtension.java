/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.tutorials.shared.Constants;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Artem Zatsarynnyy */
@Singleton
public class CodenvyTutorialProjectTypeExtension implements ProjectTypeExtension {
    private String baseUrl;

    @Inject
    public CodenvyTutorialProjectTypeExtension(@Named("extension-url") String baseUrl, ProjectTypeDescriptionRegistry registry) {
        this.baseUrl = baseUrl;
        registry.registerProjectType(this);
    }


    @Override
    public ProjectType getProjectType() {
        return new ProjectType(Constants.TUTORIAL_ID, Constants.TUTORIAL_NAME);
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
        final List<ProjectTemplateDescription> list = new ArrayList<>(8);
        list.add(new ProjectTemplateDescription("zip",
                                                "NOTIFICATION API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Notification API.",
                                                baseUrl + "/notification-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "ACTION API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Action API.",
                                                baseUrl + "/action-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "WIZARD API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Wizard API.",
                                                baseUrl + "/wizard-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "NEW RESOURCE WIZARD TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to implement a new resource wizard.",
                                                baseUrl + "/new-resource-wizard-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "PART API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Part API.",
                                                baseUrl + "/parts-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "EDITOR API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Editor API.",
                                                baseUrl + "/editor-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "WYSIWYG EDITOR TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to implement WYSIWYG editor.",
                                                baseUrl + "/wysiwyg-editor-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "GIN TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use GIN with Codenvy API.",
                                                baseUrl + "/gin-tutorial.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "THEME API TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use Theme with Codenvy API.",
                                                baseUrl + "/theme-api-tutorial.zip"));


        return list;
    }
}
