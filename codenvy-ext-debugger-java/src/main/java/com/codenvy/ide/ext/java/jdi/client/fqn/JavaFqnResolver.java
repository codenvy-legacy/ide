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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.resources.model.File;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class JavaFqnResolver implements FqnResolver {
    /** {@inheritDoc} */
    @NotNull
    @Override
    public String resolveFqn(@NotNull final File file) {
        final JavaProject project = (JavaProject)file.getProject();
        Array<String> sourceFolders = project.getDescription().getSourceFolders().getKeys();

        String fqn = "";
        for (String sourceFolder : sourceFolders.asIterable()) {
            if (file.getPath().startsWith(project.getPath() + "/" + sourceFolder)) {
                fqn = file.getPath().substring((project.getPath() + "/" + sourceFolder + "/").length());
                break;
            }
        }

        fqn = fqn.replaceAll("/", ".");
        fqn = fqn.substring(0, fqn.lastIndexOf('.'));
        return fqn;
    }
}