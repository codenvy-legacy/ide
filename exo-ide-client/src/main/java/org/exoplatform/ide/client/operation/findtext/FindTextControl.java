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
package org.exoplatform.ide.client.operation.findtext;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.editor.client.api.EditorCapability;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 */
public class FindTextControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                              ViewOpenedHandler, ViewClosedHandler {
    // public static final String ID = "Edit/Find&#47Replace...";
    public static final String ID = "Edit/Find-Replace...";

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.findReplaceControl();

    private boolean findTextViewOpened = false;

    /**
     *
     */
    public FindTextControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setDelimiterBefore(true);
        setImages(IDEImageBundle.INSTANCE.findText(), IDEImageBundle.INSTANCE.findTextDisabled());
        setEvent(new FindTextEvent());
        setHotKey("Ctrl+F");
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client
     *      .editor.event.EditorActiveFileChangedEvent)
     */
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null || event.getEditor() == null
            || !event.getEditor().isCapable(EditorCapability.FIND_AND_REPLACE)) {
            setVisible(false);
            setEnabled(false);
            return;
        } else {
            setVisible(true);
        }

        if (event.getEditor().isReadOnly()) {
            setEnabled(false);
            return;
        }

        boolean canFindReplace = event.getEditor().isCapable(EditorCapability.FIND_AND_REPLACE);
        // boolean isOpened = openedForms.contains(FindTextForm.ID);
        boolean enableSearch = canFindReplace && !findTextViewOpened;
        setEnabled(enableSearch);
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof FindTextPresenter.Display) {
            findTextViewOpened = true;
            setEnabled(false);
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof FindTextPresenter.Display) {
            findTextViewOpened = false;
            setEnabled(true);
        }
    }

}