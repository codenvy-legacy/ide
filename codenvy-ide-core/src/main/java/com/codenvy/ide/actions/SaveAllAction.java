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
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.collections.StringMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class SaveAllAction extends Action {

    private final EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public SaveAllAction(EditorAgent editorAgent, Resources resources, AnalyticsEventLogger eventLogger) {
        super("Save All", "Save all changes for project", null, resources.save());
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Save all");
        StringMap<EditorPartPresenter> editors = editorAgent.getOpenedEditors();
        editors.iterate(new StringMap.IterationCallback<EditorPartPresenter>() {
            @Override
            public void onIteration(String key, EditorPartPresenter value) {
                if (value.isDirty()) {
                    value.doSave();
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        boolean hasDirtyEditor = false;
        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
            if (editor.isDirty()) {
                hasDirtyEditor = true;
                break;
            }
        }
        e.getPresentation().setEnabled(hasDirtyEditor);
    }
}
