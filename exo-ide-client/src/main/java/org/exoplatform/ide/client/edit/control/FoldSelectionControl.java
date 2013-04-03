/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.client.edit.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFoldSelectionEvent;
import org.exoplatform.ide.editor.client.api.EditorCapability;

/**
 * Control to make a fold from any text selection.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldSelectionControl.java Feb 28, 2013 5:00:20 PM azatsarynnyy $
 */
public class FoldSelectionControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Edit/FoldSelection";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.foldSelectionControlTitle();

    public FoldSelectionControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.blankImage(), IDEImageBundle.INSTANCE.blankImage());
        setEvent(new EditorFoldSelectionEvent());
        setShowInContextMenu(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client
     * .editor.event.EditorActiveFileChangedEvent) */
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null || event.getEditor() == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        if (event.getEditor().isReadOnly()) {
            setEnabled(false);
        }

        boolean isFoldingSupported = event.getEditor().isCapable(EditorCapability.CODE_FOLDING);
        setVisible(isFoldingSupported);
        setEnabled(isFoldingSupported);
    }
}
