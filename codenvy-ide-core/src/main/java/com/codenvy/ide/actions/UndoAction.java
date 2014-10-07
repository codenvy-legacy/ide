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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.api.texteditor.UndoableEditor;
import com.google.inject.Inject;

/**
 * Undo Action
 *
 * @author Roman Nikitenko
 */

public class UndoAction extends Action {

    private       EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public UndoAction(EditorAgent editorAgent,
                      CoreLocalizationConstant localization,
                      AnalyticsEventLogger eventLogger,
                      Resources resources) {
        super(localization.undoName(), localization.undoDescription(), null, resources.undo());
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();

        if (activeEditor != null && activeEditor instanceof UndoableEditor) {
            final HandlesUndoRedo undoRedo = ((UndoableEditor)activeEditor).getUndoRedo();
            if (undoRedo != null) {
                undoRedo.undo();
            }
        }
    }

    @Override
    public void update(ActionEvent e) {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();

        boolean mustEnable = false;
        if (activeEditor != null && activeEditor instanceof UndoableEditor) {
            final HandlesUndoRedo undoRedo = ((UndoableEditor)activeEditor).getUndoRedo();
            if (undoRedo != null) {
                mustEnable = undoRedo.undoable();
            }
        }
        e.getPresentation().setEnabled(mustEnable);
    }
}
