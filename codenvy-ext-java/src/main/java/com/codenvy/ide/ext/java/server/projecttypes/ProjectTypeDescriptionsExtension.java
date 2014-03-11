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
package com.codenvy.ide.ext.java.server.projecttypes;

import com.codenvy.api.project.server.ProjectTypeDescriptionExtension;
import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.shared.AttributeDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.java.shared.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author Artem Zatsarynnyy */
@Singleton
public class ProjectTypeDescriptionsExtension implements ProjectTypeDescriptionExtension {

    private Map<String, String> icons = new HashMap<>();

    @Inject
    public ProjectTypeDescriptionsExtension(ProjectTypeDescriptionRegistry registry) {
        icons.put("war.projecttype.big.icon", "java-extension/web_app_big.png");
        registry.registerDescription(this);
    }

    @Override
    public List<ProjectType> getProjectTypes() {
        final List<ProjectType> list = new ArrayList<>(3);
        list.add(new ProjectType(Constants.JAR_ID, Constants.JAR_NAME));
        list.add(new ProjectType(Constants.WAR_ID, Constants.WAR_NAME));
        list.add(new ProjectType(Constants.SPRING_ID, Constants.SPRING_NAME));
        return list;
    }

    @Override
    public List<AttributeDescription> getAttributeDescriptions() {
        final List<AttributeDescription> list = new ArrayList<>(7);
        list.add(new AttributeDescription(Constants.LANGUAGE));
        list.add(new AttributeDescription(Constants.LANGUAGE_VERSION));
        list.add(new AttributeDescription(Constants.FRAMEWORK));
        list.add(new AttributeDescription(Constants.BUILDER_NAME));
        list.add(new AttributeDescription(Constants.BUILDER_ANT_SOURCE_FOLDERS));
        list.add(new AttributeDescription(Constants.BUILDER_MAVEN_SOURCE_FOLDERS));
        list.add(new AttributeDescription(Constants.RUNNER_NAME));
        return list;
    }
}
