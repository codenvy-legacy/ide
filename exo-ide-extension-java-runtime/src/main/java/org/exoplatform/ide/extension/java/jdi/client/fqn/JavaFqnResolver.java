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
package org.exoplatform.ide.extension.java.jdi.client.fqn;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:05:01 PM Mar 28, 2012 evgen $
 */
public class JavaFqnResolver implements FqnResolver {
    /** Default Maven 'sourceDirectory' value */
    private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    /** @see org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolver#resolveFqn(org.exoplatform.ide.vfs.client.model.FileModel) */
    @Override
    public String resolveFqn(FileModel file) {
        ProjectModel project = file.getProject();
        String sourcePath =
                project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : DEFAULT_SOURCE_FOLDER;


        String pack = file.getPath().substring((project.getPath() + "/" + sourcePath + "/").length());
        pack = pack.replaceAll("/", ".");
        pack = pack.substring(0, pack.lastIndexOf('.'));
        return pack;
    }
}
