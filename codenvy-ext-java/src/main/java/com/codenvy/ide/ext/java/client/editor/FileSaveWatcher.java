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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.codenvy.ide.api.resources.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class FileSaveWatcher {

    @Inject
    private JavaParserWorker worker;

    void editorOpened(final TextEditorPartPresenter editor) {
        final PropertyListener propertyListener = new PropertyListener() {
            @Override
            public void propertyChanged(PartPresenter source, int propId) {
                if (propId == EditorPartPresenter.PROP_DIRTY) {
                    if (!editor.isDirty()) {
                        File file = editor.getEditorInput().getFile();
                        String fqn = file.getParent().getName() + '.' + file.getName().substring(0, file.getName().indexOf('.'));
                        worker.removeFanFromCache(fqn);
                    }
                }
            }
        };
        editor.addPropertyListener(propertyListener);
    }


}
