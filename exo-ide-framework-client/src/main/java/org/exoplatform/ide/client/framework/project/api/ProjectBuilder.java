/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.client.framework.project.api;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.HashMap;
import java.util.Map;


/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ProjectBuilder {

    public interface Builder {

        IDEProject build(ProjectModel project);

    }

    private static Map<String, Builder> builders = new HashMap<String, ProjectBuilder.Builder>();

    public static void addBuilder(String projectType, Builder builder) {
        builders.put(projectType, builder);
    }

    public static IDEProject createProject(ProjectModel project) {
        if (builders.containsKey(project.getProjectType())) {
            return builders.get(project.getProjectType()).build(project);
        }

        return new IDEProject(project);
    }

}
