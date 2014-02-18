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
import org.exoplatform.ide.client.editor.EditorView;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.editor.client.api.Editor;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"workspace/developer"})
public class UndoTypingControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                EditorFileContentChangedHandler, ShowContextMenuHandler,
                                                                ViewActivatedHandler {

    public static final String ID = "Edit/Undo Typing";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.undoTypingControl();

    private boolean isEditorPanelActive = false;

    /**
     *
     */
    public UndoTypingControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setDelimiterBefore(true);
        setImages(IDEImageBundle.INSTANCE.undo(), IDEImageBundle.INSTANCE.undoDisabled());
        setEvent(new EditorUndoTypingEvent());
        setGroupName(GroupNames.EDIT);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
        IDE.addHandler(ShowContextMenuEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        // TODO Version
        if (event.getFile() == null /* || (event.getFile() instanceof Version) */) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        setVisible(true);
        if (event.getEditor() != null) {
            setEnabled(event.getEditor().hasUndoChanges());
        } else {
            setEnabled(false);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorFileContentChangedEvent)
     */
    @Override
    public void onEditorFileContentChanged(EditorFileContentChangedEvent event) {
        setEnabled(event.hasUndoChanges());
    }

    /**
     * @see org.exoplatform.ide.client.framework.event.ShowContextMenuHandler#onShowContextMenu(org.exoplatform.ide.client.framework
     *      .event.ShowContextMenuEvent)
     */
    @Override
    public void onShowContextMenu(ShowContextMenuEvent event) {
        boolean showInContextMenu = (event.getObject() instanceof Editor);
        setShowInContextMenu(showInContextMenu && isEditorPanelActive);
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     *      .ui.api.event.ViewActivatedEvent)
     */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        isEditorPanelActive = event.getView() instanceof EditorView;
        setShowInContextMenu(isEditorPanelActive);
    }
}
