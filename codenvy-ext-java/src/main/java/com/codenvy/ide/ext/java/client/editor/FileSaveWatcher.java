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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.codenvy.ide.resources.model.File;
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
