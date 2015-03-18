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
package org.eclipse.che.ide.restore;

import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * DTO describes the state of the project.
 *
 * @author Artem Zatsarynnyy
 */
@DTO
public interface ProjectState {

    /** Get paths of all opened files. */
    List<String> getOpenedFilesPaths();

    void setOpenedFilesPaths(List<String> openedFilesPaths);

    ProjectState withOpenedFilesPaths(List<String> openedFilesPaths);


    /** Get path of the active file. */
    String getActiveFilePath();

    void setActiveFilePath(String activeFilePath);

    ProjectState withActiveFilePath(String activeFilePath);


    /** Get cursor position of the active file. */
    int getCursorOffset();

    void setCursorOffset(int cursorOffset);

    ProjectState withCursorOffset(int cursorOffset);
}
