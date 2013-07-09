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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.File;

/**
 * Editor Agent manages Editors, it allows to open a new editor with given file,
 * retrieve current active editor and find all the opened editors.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface EditorAgent {

    /**
     * Open editor with given file
     *
     * @param file
     */
    public void openEditor(final File file);

    /**
     * Get all opened editors
     *
     * @return map with all opened editors
     */
    public JsonStringMap<EditorPartPresenter> getOpenedEditors();

    /**
     * Current active editor
     *
     * @return the current active editor
     */
    public EditorPartPresenter getActiveEditor();

}