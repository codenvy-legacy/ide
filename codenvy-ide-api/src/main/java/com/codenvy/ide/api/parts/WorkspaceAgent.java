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
package com.codenvy.ide.api.parts;


import com.codenvy.ide.api.action.Constraints;
import com.codenvy.ide.api.extension.SDK;


/**
 * Handles IDE Perspective, allows to open/close/switch Parts,
 * manages opened Parts.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@SDK(title = "ide.api.ui.workspace")
public interface WorkspaceAgent {

    /**
     * Activate given part
     *
     * @param part
     */
    void setActivePart(PartPresenter part);

    /**
     * Opens given Part
     *
     * @param part
     * @param type
     */
    void openPart(PartPresenter part, PartStackType type);

    /**
     * Opens Part with constraint
     *
     * @param part
     * @param type
     * @param constraint
     */
    void openPart(PartPresenter part, PartStackType type, Constraints constraint);

    /**
     * Hides given Part
     *
     * @param part
     */
    void hidePart(PartPresenter part);

    /**
     * Remove given Part
     *
     * @param part
     */
    void removePart(PartPresenter part);
}