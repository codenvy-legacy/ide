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
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ProjectAction;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Save editor content Action
 *
 * @author Evgen Vidolob
 */
@Singleton
public class SaveAction extends ProjectAction {

    private final EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public SaveAction(Resources resources, EditorAgent editorAgent, AnalyticsEventLogger eventLogger) {
        super("Save", "Save changes for current file", resources.save());
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        editorAgent.getActiveEditor().doSave();
    }

    @Override
    public void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(true);
        EditorPartPresenter editor = editorAgent.getActiveEditor();
        e.getPresentation().setEnabled(editor != null && editor.isDirty());
    }
}
