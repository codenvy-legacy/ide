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
package com.codenvy.ide.ext.java.jdi.client.fqn;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:05:01 PM Mar 28, 2012 evgen $
 */
@Singleton
public class JavaFqnResolver implements FqnResolver {
    /** Default Maven 'sourceDirectory' value */
    private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String resolveFqn(@NotNull File file) {
        Project project = file.getProject();
        String sourcePath = project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder") : DEFAULT_SOURCE_FOLDER;

        String pack = file.getPath().substring((project.getPath() + "/" + sourcePath + "/").length());
        pack = pack.replaceAll("/", ".");
        pack = pack.substring(0, pack.lastIndexOf('.'));
        return pack;
    }
}