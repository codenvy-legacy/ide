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

package org.exoplatform.ide.client.operation.autocompletion;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AutocompletionHandler implements AutocompleteCalledHandler, EditorActiveFileChangedHandler {

    private Editor activeEditor;

    public AutocompletionHandler() {
//      IDE.getInstance().addControl(new OpenAutocompleteControl());

        IDE.addHandler(AutocompleteCalledEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeEditor = event.getEditor();
    }

    @Override
    public void onAutocompleteCalled(AutocompleteCalledEvent event) {
        if (activeEditor != null && activeEditor instanceof CodeMirror) {
            CodeMirror codemirror = (CodeMirror)activeEditor;
            codemirror.onAutocomplete();
        } else {
            IDE.fireEvent(new RunCodeAssistantEvent());
        }
    }

}
