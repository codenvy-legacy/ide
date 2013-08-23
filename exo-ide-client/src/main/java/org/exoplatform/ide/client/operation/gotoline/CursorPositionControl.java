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
package org.exoplatform.ide.client.operation.gotoline;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.component.TextButton.TextAlignment;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class CursorPositionControl extends StatusTextControl implements IDEControl, EditorCursorActivityHandler,
                                                                        EditorActiveFileChangedHandler {

    public static final String ID = "__editor_cursor_position";

    private FileModel file;

    private Editor editor;

    /**
     *
     */
    public CursorPositionControl() {
        super(ID);

        setSize(70);
        setFireEventOnSingleClick(true);
        setText("&nbsp;");
        setTextAlignment(TextAlignment.CENTER);
        setEvent(new GoToLineEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorCursorActivityEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

        setEnabled(true);
    }

    /**
     * @param row
     * @param column
     */
    private void setCursorPosition(int row, int column) {
        if (row > 0 && column > 0) {
            setText("<nobr>" + row + " : " + column + "</nobr>");
        } else {
            setText("");
        }
    }

    /** @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event
     * .EditorActivityEvent) */
    @Override
    public void onEditorCursorActivity(EditorCursorActivityEvent event) {
        if (editor == null || !editor.getId().equals(event.getEditor().getId())) {
            return;
        }

        setCursorPosition(event.getRow(), event.getColumn());
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        file = event.getFile();
        editor = event.getEditor();
        Scheduler.get().scheduleDeferred(updateCursorPositionCommand);
    }

    ScheduledCommand updateCursorPositionCommand = new ScheduledCommand() {
        @Override
        public void execute() {
            try {
                if (file == null || editor == null) {
                    setText("");
                    setVisible(false);
                    return;
                }

                setVisible(true);
                setCursorPosition(editor.getCursorRow(), editor.getCursorColumn());
            } catch (Throwable e) {
            }
        }
    };

}
