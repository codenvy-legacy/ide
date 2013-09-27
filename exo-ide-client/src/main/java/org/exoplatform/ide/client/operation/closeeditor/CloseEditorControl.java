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
package org.exoplatform.ide.client.operation.closeeditor;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class CloseEditorControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                 VfsChangedHandler {

    public final static String ID = "File/Close";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.closeEditorControlTitle();

    private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.closeEditorControlPrompt();

    public CloseEditorControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setEvent(new CloseEditorEvent());
        setGroupName(GroupNames.COMMANDS);
        setHotKey("Ctrl+W");
    }

    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        setEnabled(event.getFile() != null);
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        setVisible(event.getVfsInfo() != null);
    }

}
