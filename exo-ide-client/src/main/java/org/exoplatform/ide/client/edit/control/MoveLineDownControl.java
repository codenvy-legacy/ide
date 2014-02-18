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
import org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownEvent;
import org.exoplatform.ide.editor.ckeditor.CKEditor;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
@RolesAllowed({"workspace/developer"})
public class MoveLineDownControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Edit/Move Line Down";

    private String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.moveLineDownControl();

    public MoveLineDownControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setEvent(new EditorMoveLineDownEvent());
        setHotKey("Alt+Down");
        setImages(IDEImageBundle.INSTANCE.lineDown(), IDEImageBundle.INSTANCE.lineDownDisabled());
    }

    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        boolean isEnabled = event.getFile() != null && event.getEditor() != null && !(event.getEditor() instanceof CKEditor);
        setVisible(isEnabled);
        setEnabled(isEnabled);
    }

}
