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
package org.exoplatform.ide.client.edit.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineEvent;
import org.exoplatform.ide.editor.client.api.EditorCapability;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class DeleteCurrentLineControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Edit/Delete Current Line";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.deleteCurrentLineControl();

    public DeleteCurrentLineControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.deleteCurrentLine(), IDEImageBundle.INSTANCE.deleteCurrentLineDisabled());
        setEvent(new EditorDeleteCurrentLineEvent());
        setHotKey("Ctrl+D");
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client
     * .editor.event.EditorActiveFileChangedEvent) */
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {

        if (event.getFile() == null || event.getEditor() == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        if (event.getEditor().isCapable(EditorCapability.DELETE_LINES)) {
            setVisible(true);
            setEnabled(true);
        } else {
            setVisible(false);
            setEnabled(false);
        }

        if (event.getEditor().isReadOnly()) {
            setEnabled(false);
            return;
        }
    }
}
