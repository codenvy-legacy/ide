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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFormatTextEvent;
import org.exoplatform.ide.editor.client.api.EditorCapability;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"workspace/developer"})
public class FormatSourceControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler {

    public static final String ID = "Edit/Format";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.formatControl();

    /**
     *
     */
    public FormatSourceControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.format(), IDEImageBundle.INSTANCE.formatDisabled());
        setEvent(new EditorFormatTextEvent());
        setHotKey("Ctrl+Shift+F");
        setGroupName(GroupNames.EDIT);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        // TODO versions
        if (event.getFile() == null || event.getEditor() == null /* || (event.getFile() instanceof Version) */) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        if (event.getEditor().isCapable(EditorCapability.FORMAT_SOURCE)) {
            String mimeType = event.getFile().getMimeType();
            //TODO add possibility to configure editor capability's
            if (MimeType.TEXT_PLAIN.equals(mimeType) || MimeType.TEXT_X_PYTHON.equals(mimeType)) {
                setVisible(false);
                setEnabled(false);
            } else {
                setVisible(true);
                setEnabled(true);
            }
        } else {
            setVisible(false);
            setEnabled(false);
        }
    }
}
