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
package com.codenvy.ide.ext.extensions.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.extensions.shared.Constants;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Artem Zatsarynnyy */
@Singleton
public class CodenvyExtProjectTypeExtension implements ProjectTypeExtension {
    private String baseUrl;

    @Inject
    public CodenvyExtProjectTypeExtension(@Named("extension-url") String baseUrl, ProjectTypeDescriptionRegistry registry) {
        this.baseUrl = baseUrl;
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType(Constants.CODENVY_EXTENSION_ID, Constants.CODENVY_EXTENSION_NAME);
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
        return list;
    }
}
