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

package org.exoplatform.ide.client.edit.switching;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GoNextEditorControl extends SimpleControl
        implements IDEControl, EditorFileOpenedHandler, EditorFileClosedHandler, EditorActiveFileChangedHandler {

    public static final String ID = "Window/Navigation/Next Editor";

    public static final String TITLE = "Next Editor";

    public static final String PROMPT = "Switch to Next Editor";

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    private File activeFile;

    public GoNextEditorControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.next(), IDEImageBundle.INSTANCE.nextDisabled());
        setEvent(new GoNextEditorEvent());
        setHotKey("Ctrl+Shift+PageDown");
    }

    @Override
    public void initialize() {
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        setVisible(true);
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
        update();
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
        update();
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        update();
    }

    private void update() {
        if (activeFile == null) {
            setEnabled(false);
            return;
        }

        String[] keys = openedFiles.keySet().toArray(new String[openedFiles.size()]);
        if (keys.length == 1) {
            setEnabled(false);
            return;
        }

        int pos = 0;
        for (String key : keys) {
            if (activeFile.getId().equals(key) && pos < keys.length - 1) {
                setEnabled(true);
                return;
            }

            pos++;
        }

        setEnabled(false);
    }

}
