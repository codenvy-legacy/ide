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
import org.exoplatform.ide.client.framework.util.ProjectResolver;

import java.util.Comparator;

/**
 * Comparator for ordering project types.
 *
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ProjectTypesComparator.java May 17, 2013 4:47:56 PM vsvydenko $
 */
final class ProjectTypesComparator implements Comparator<ProjectType> {
    /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
    @Override
    public int compare(ProjectType type1, ProjectType type2) {
        int indexOfProjectType1 = ProjectResolver.getIndexOfProjectType(type1);
        int indexOfProjectType2 = ProjectResolver.getIndexOfProjectType(type2);

        if (indexOfProjectType1 < indexOfProjectType2) {
            return -1;
        } else if (indexOfProjectType1 > indexOfProjectType2) {
            return 1;
        }

        return 0;
    }
}
