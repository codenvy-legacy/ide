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
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ProjectAction;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author Evgen Vidolob */
@Singleton
public class SaveAllAction extends ProjectAction {

    private final EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public SaveAllAction(EditorAgent editorAgent, Resources resources, AnalyticsEventLogger eventLogger) {
        super("Save All", "Save all changes for project", resources.save());
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        Array<EditorPartPresenter> values = editorAgent.getOpenedEditors().getValues();
        Array<EditorPartPresenter> editors = Collections.createArray(values.asIterable());
        save(editors);
    }

    private void save(final Array<EditorPartPresenter> editors) {
        if (editors.isEmpty()) {
            return;
        }

        final EditorPartPresenter editorPartPresenter = editors.get(0);
        if (editorPartPresenter.isDirty()) {
            editorPartPresenter.doSave(new AsyncCallback<EditorInput>() {
                @Override
                public void onFailure(Throwable caught) {
                    Log.error(SaveAllAction.class, caught);
                    //try to save other files
                    editors.remove(editorPartPresenter);
                    save(editors);
                }

                @Override
                public void onSuccess(EditorInput result) {
                    editors.remove(editorPartPresenter);
                    save(editors);
                }
            });
        } else {
            editors.remove(editorPartPresenter);
            save(editors);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateProjectAction(ActionEvent e) {
        e.getPresentation().setVisible(true);
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
