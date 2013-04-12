/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client.project.create;

import org.exoplatform.ide.client.framework.project.ProjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Here lists the types of projects that may be modules of Maven project.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
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
