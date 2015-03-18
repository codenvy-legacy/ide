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
package org.eclipse.che.ide.statepersisting;

import org.eclipse.che.dto.shared.DTO;

import java.util.Map;

/**
 * DTO describes the state of the project.
 *
 * @author Artem Zatsarynnyy
 */
@DTO
public interface ProjectState {

    /** Get project states. */
    Map<String, Map<String, String>> getActions();

    void setActions(Map<String, Map<String, String>> actions);

    ProjectState withActions(Map<String, Map<String, String>> actions);
}
