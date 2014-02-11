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

import com.codenvy.api.project.server.ProjectTemplateRegistry;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectTemplateExtension;
import com.codenvy.api.project.shared.ProjectType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/** @author Artem Zatsarynnyy */
@Singleton
public class WarProjectTemplateExtension implements ProjectTemplateExtension {

    @Inject
    public WarProjectTemplateExtension(ProjectTemplateRegistry registry) {
        registry.register(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType("war", "Java Web Application (WAR)");
    }

    @Override
    public List<ProjectTemplateDescription> getTemplateDescriptions() {
        final List<ProjectTemplateDescription> list = new ArrayList<>(1);
        list.add(new ProjectTemplateDescription("maven_war",
                                                "JAVA WEB PROJECT",
                                                "Java Web project.",
                                                "templates/MavenWar.zip"));
        return list;
    }
}
