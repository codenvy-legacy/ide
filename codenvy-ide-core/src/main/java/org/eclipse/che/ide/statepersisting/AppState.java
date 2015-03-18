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
 * DTO describes Codenvy application's state that may be saved/restored.
 *
 * @author Artem Zatsarynnyy
 */
@DTO
public interface AppState {

    /** Get last opened project path or {@code null} if none. */
    String getLastProjectPath();

    void setLastProjectPath(String lastProjectPath);

    AppState withLastProjectPath(String lastProjectPath);


    /** Get project states. */
    Map<String, ProjectState> getProjects();

    void setProjects(Map<String, ProjectState> projects);

    AppState withProjects(Map<String, ProjectState> projects);
}
