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
package com.codenvy.ide.actions;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.json.JsonStringMap;
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
        super("Save All", "Save all changes for project", resources.file());
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
