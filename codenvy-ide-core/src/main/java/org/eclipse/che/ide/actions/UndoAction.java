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
package org.eclipse.che.ide.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.texteditor.HandlesUndoRedo;
import org.eclipse.che.ide.api.texteditor.UndoableEditor;

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
