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

import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Provide possibility for importing source from some resource.
 *
 * @author Roman Nikitenko
 */
public interface ProjectImporter {
    /**
     * @return unique id of importer e.g git, zip
     */
    String getId();

    /**
     * Imports source for the given {@code project}.
     *
     * @param projectName
     *         name of the project
     * @param project
     *         the project for import
     * @param callback
     *         callback
     */
    void importSources(String projectName, ImportProject project, AsyncCallback<ProjectDescriptor> callback);
}
