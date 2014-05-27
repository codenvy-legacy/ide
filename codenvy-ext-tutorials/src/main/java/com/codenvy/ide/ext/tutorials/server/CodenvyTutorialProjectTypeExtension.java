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
import java.util.List;

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
        return new ProjectType(Constants.TUTORIAL_ID, Constants.TUTORIAL_NAME, Constants.CODENVY_TUTORIAL_CATEGORY);
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
                                                "PART API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Part API.",
                                                baseUrl + "/parts-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "EDITOR API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Editor API.",
                                                baseUrl + "/editor-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "WYSIWYG EDITOR TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to implement " +
                                                "WYSIWYG editor.",
                                                baseUrl + "/wysiwyg-editor-tutorial.zip"));

        list.add(new ProjectTemplateDescription("zip",
                                                "GIN TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use GIN with" +
                                                " Codenvy API.",
                                                baseUrl + "/gin-tutorial.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "THEME API TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use Theme " +
                                                "with Codenvy API.",
                                                baseUrl + "/theme-api-tutorial.zip"));
        list.add(new ProjectTemplateDescription("zip",
                                                "ICON REGISTRY API TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use " +
                                                "IconRegistry.",
                                                baseUrl + "/icons-registry-api-tutorial.zip"));
         list.add(new ProjectTemplateDescription("zip",
                                                "SERVER SIDE API TUTORIAL",
                                                "The following tutorial will take you through a simple example demonstrating registration" + 
                                                "of a server side component and server-client side communication.",
                                                baseUrl + "/server-side-tutorial.zip"));

        return list;
    }
}
