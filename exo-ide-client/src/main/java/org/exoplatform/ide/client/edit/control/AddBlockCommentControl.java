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
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorAddBlockCommentEvent;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityHandler;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 10:56:42 AM anya $
 */
@RolesAllowed({"workspace/developer"})
public class AddBlockCommentControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                     EditorCursorActivityHandler {

    public static final  String ID    = "Edit/Add Block Comment";
    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.addBlockCommentControl();

    public AddBlockCommentControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setEvent(new EditorAddBlockCommentEvent());
        setHotKey("Ctrl+Shift+/");
        setImages(IDEImageBundle.INSTANCE.addBlockComment(), IDEImageBundle.INSTANCE.addBlockCommentDisabled());
        setGroupName(GroupNames.COMMENT);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorCursorActivityEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        boolean isEnabled = event.getFile() != null && event.getEditor() != null
                            && event.getEditor().isCapable(EditorCapability.COMMENT_SOURCE);
        setVisible(isEnabled);
        updateEnableState(event.getEditor());
    }


    /** {@inheritDoc} */
    @Override
    public void onEditorCursorActivity(EditorCursorActivityEvent event) {
        updateEnableState(event.getEditor());
    }

    private void updateEnableState(Editor editor) {
        if (editor == null) {
            setEnabled(false);
            return;
        }
        
        SelectionRange selectionRange = editor.getSelectionRange();
        if (selectionRange == null) {
            setEnabled(false);
            return;
        }
        
        boolean hasSelection = (selectionRange.getStartSymbol() != selectionRange.getEndSymbol())
                                   || (selectionRange.getStartLine() != selectionRange.getEndLine());
        setEnabled(hasSelection);
    }

}
