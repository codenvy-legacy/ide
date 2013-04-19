/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.api.ui.perspective;


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
    public void setActivePart(PartPresenter part);

    /**
     * Opens given Part
     *
     * @param part
     * @param type
     */
    public void openPart(PartPresenter part, PartStackType type);

    /**
     * Hides given Part
     * @param part
     */
    public void hidePart(PartPresenter part);

    /**
     * Remove given Part
     * @param part
     */
    public void removePart(PartPresenter part);
}