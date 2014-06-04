/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.client.fqn;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.api.resources.model.File;
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