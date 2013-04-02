/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client.operation.closeeditor;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.ClearFocusForm;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class CloseEditorController implements CloseEditorHandler, EditorActiveFileChangedHandler {

    private FileModel activeFile;

    public CloseEditorController() {
        IDE.getInstance().addControl(new CloseEditorControl());

        IDE.addHandler(CloseEditorEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
    }

    @Override
    public void onCloseEditor(CloseEditorEvent event) {
        if (activeFile == null) {
            return;
        }

        IDE.fireEvent(new EditorCloseFileEvent(activeFile));
        ClearFocusForm.getInstance().clearFocus();
    }

}
