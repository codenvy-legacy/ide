/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.actions;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.collections.JsonStringMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class SaveAllAction extends Action {

    private final EditorAgent editorAgent;
    private final Resources   resources;

    @Inject
    public SaveAllAction(EditorAgent editorAgent, Resources resources) {
        super("Save All", "Save all changes for project", resources.saveAll());
        this.editorAgent = editorAgent;
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        JsonStringMap<EditorPartPresenter> editors = editorAgent.getOpenedEditors();
        editors.iterate(new JsonStringMap.IterationCallback<EditorPartPresenter>() {
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
