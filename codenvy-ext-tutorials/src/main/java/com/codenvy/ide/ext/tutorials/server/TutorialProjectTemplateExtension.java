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

import com.codenvy.api.project.server.ProjectTemplateRegistry;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectTemplateExtension;
import com.codenvy.api.project.shared.ProjectType;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/** @author Artem Zatsarynnyy */
@Singleton
public class TutorialProjectTemplateExtension implements ProjectTemplateExtension {

    private final String baseUrl;

    @Inject
    public TutorialProjectTemplateExtension(@Named("extension-url") String baseUrl, ProjectTemplateRegistry registry) {
        this.baseUrl = baseUrl;
        registry.register(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType("codenvy_tutorial", "Codenvy tutorial");
    }

    @Override
    public List<ProjectTemplateDescription> getTemplateDescriptions() {
        final List<ProjectTemplateDescription> list = new ArrayList<>(9);
        list.add(new ProjectTemplateDescription("NotificationTutorial",
                                                "NOTIFICATION API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Notification API.",
                                                baseUrl + "/notification-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("ActionTutorial",
                                                "ACTION API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Action API.",
                                                baseUrl + "/action-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("WizardTutorial",
                                                "WIZARD API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Wizard API.",
                                                baseUrl + "/wizard-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("NewResourceWizardTutorial",
                                                "NEW RESOURCE WIZARD TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to implement a new resource wizard.",
                                                baseUrl + "/new-resource-wizard-tutorial.zip"));

        list.add(new ProjectTemplateDescription("PartsTutorial",
                                                "PART API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Part API.",
                                                baseUrl + "/parts-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("EditorTutorial",
                                                "EDITOR API TUTORIAL",
                                                "Tutorial that is demonstrating how to use Codenvy Editor API.",
                                                baseUrl + "/editor-api-tutorial.zip"));

        list.add(new ProjectTemplateDescription("WysiwygEditorTutorial",
                                                "WYSIWYG EDITOR TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to implement WYSIWYG editor.",
                                                baseUrl + "/wysiwyg-editor-tutorial.zip"));

        list.add(new ProjectTemplateDescription("GinTutorial",
                                                "GIN TUTORIAL",
                                                "The following tutorial will take you through simple example to learn how to use GIN with Codenvy API.",
                                                baseUrl + "/gin-tutorial.zip"));
        return list;
    }
}
