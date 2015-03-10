/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.build;

import org.eclipse.che.api.builder.dto.BuildTaskDescriptor;

/**
 * Context interface, used for communication between different action that used builder to avoid
 * running several builder tasks in one time.
 *
 * @author Evgen Vidolob
 */
public interface BuildContext {

    /**
     * return true if build started
     *
     * @return the boolean
     */
    boolean isBuilding();

    /**
     * Change building state(started of finished)
     *
     * @param building
     *         the building
     */
    void setBuilding(boolean building);

    /**
     * Returns descriptor of the last build task.
     *
     * @return descriptor of the last build task
     */
    BuildTaskDescriptor getBuildTaskDescriptor();

    /**
     * Sets descriptor of build task.
     *
     * @param buildTaskDescriptor descriptor of build task
     */
    void setBuildTaskDescriptor(BuildTaskDescriptor buildTaskDescriptor);
}
