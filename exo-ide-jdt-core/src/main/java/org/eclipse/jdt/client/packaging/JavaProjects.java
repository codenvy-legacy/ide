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
package org.eclipse.jdt.client.packaging;

import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class JavaProjects {

    private static ArrayList<String> projects = new ArrayList<String>();

    static {
        projects.add(ProjectResolver.APP_ENGINE_JAVA);
        projects.add(ProjectResolver.SERVLET_JSP);
        projects.add(ProjectResolver.SPRING);
        projects.add(ProjectType.JAVA.value());
        projects.add(ProjectType.JAR.value());
        projects.add(ProjectType.WAR.value());
        projects.add(ProjectType.JSP.value());
        projects.add(ProjectType.MultiModule.value());
        projects.add(ProjectType.ANDROID.value());
    }

    public static boolean contains(ProjectModel project) {
        if (project == null) {
            return false;
        }

        return projects.contains(project.getProjectType());
    }

    public static List<String> getList() {
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(projects);
        return list;
    }

}
