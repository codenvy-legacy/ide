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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.api.resources.model.Project;

/**
 * Notified when app will be launched.
 *
 * @author Artem Zatsarynnyy
 */
public interface ProjectRunCallback {
    /**
     * Notified when app will be launched.
     *
     * @param appDescriptor
     *         descriptor of application that was run
     * @param project
     *         project that was run
     */
    void onRun(ApplicationProcessDescriptor appDescriptor, Project project);
}
