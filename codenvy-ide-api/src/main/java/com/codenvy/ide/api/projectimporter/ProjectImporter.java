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
package com.codenvy.ide.api.projectimporter;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;

import javax.validation.constraints.NotNull;

/**
 * Provide possibility for importing source from some resource.
 *
 * @author Roman Nikitenko
 */
public interface ProjectImporter {
    public interface ImportCallback {
        /** Call when import operation complete successfully. */
        void onSuccess(ProjectDescriptor result);

        /**
         * Call when import operation complete failure.
         *
         * @param exception
         *         exception that happened
         */
        void onFailure(@NotNull Throwable exception);
    }

    /**
     * @return unique id of importer e.g git, zip
     */
    String getId();

    /**
     * Imports source from the given {@code url}.
     *
     * @param url
     *         project's location
     * @param projectName
     *         name of the project
     * @param callback
     *         callback
     */
    void importSources(String url, String projectName,  ProjectImporter.ImportCallback callback);
}
