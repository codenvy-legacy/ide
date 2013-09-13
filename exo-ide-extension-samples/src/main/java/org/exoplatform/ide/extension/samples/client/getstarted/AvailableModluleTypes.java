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
package org.exoplatform.ide.extension.samples.client.getstarted;

import org.exoplatform.ide.client.framework.project.ProjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Here lists the types of projects that may be modules of Maven project.
 *
 * @author <a href="mailto:vsvydenko@exoplatfrom.com">Valeriy Svydenko</a>
 * @version $Id: AvailableModluleTypes.java May 17, 2013 3:51:21 PM vsvydenko $
 */
public class AvailableModluleTypes {

    private static List<ProjectType> projectTypes = new ArrayList<ProjectType>();

    static {
        projectTypes.add(ProjectType.MultiModule);

        projectTypes.add(ProjectType.JSP);
        projectTypes.add(ProjectType.SPRING);
        projectTypes.add(ProjectType.JAR);
        projectTypes.add(ProjectType.WAR);
    }

    public static boolean contains(ProjectType projectType) {
        return projectTypes.contains(projectType);
    }

    public static boolean contains(String projectType) {
        return projectTypes.contains(ProjectType.fromValue(projectType));
    }

}
