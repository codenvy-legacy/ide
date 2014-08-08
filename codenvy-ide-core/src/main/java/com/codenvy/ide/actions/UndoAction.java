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
package com.codenvy.ide.actions;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.texteditor.UndoManager;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.google.inject.Inject;

/**
 * Undo Action
 *
 * @author Roman Nikitenko
 */

public class UndoAction extends Action {

    private EditorAgent editorAgent;

    @Inject
    public UndoAction(EditorAgent editorAgent,
                      CoreLocalizationConstant localization) {
        super(localization.undoName(), localization.undoDescription(), null);
        this.editorAgent = editorAgent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();

        if (activeEditor != null && activeEditor instanceof UndoableEditor) {
            UndoManager undoManager = ((UndoableEditor)activeEditor).getUndoManager();
            undoManager.undo();
        }
    }

    @Override
    public void update(ActionEvent e) {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();

        if (activeEditor != null && activeEditor instanceof UndoableEditor) {
            UndoManager undoManager = ((UndoableEditor)activeEditor).getUndoManager();
            e.getPresentation().setEnabled(undoManager.undoable());
        } else {
            e.getPresentation().setEnabled(false);
        }
    }
}
