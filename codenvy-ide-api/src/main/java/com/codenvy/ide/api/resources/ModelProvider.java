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
package com.codenvy.ide.api.resources;

import com.codenvy.ide.api.resources.model.Project;

/**
 * Model provider, is an entity responsible for creating a nature-specific project model.
 * Class itself creates an empty project of desired type.
 *
 * @author Nikolay Zamosenchuk
 */
public interface ModelProvider {
    /**
     * Creates empty project instance of corresponding class
     *
     * @return Project instance
     */
    public Project createProjectInstance();
}
