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
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorSelectAllEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.editor.client.api.Editor;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 3, 2012 12:35:53 PM anya $
 */
public class SelectAllTextControl extends SimpleControl implements IDEControl, ShowContextMenuHandler,
                                                                   ViewActivatedHandler, EditorActiveFileChangedHandler {
    public static final String ID = "Edit/Select All";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.selectAllControl();

    private boolean isEditorPanelActive = false;

    /** @param id */
    public SelectAllTextControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.selectAll(), IDEImageBundle.INSTANCE.selectAllDisabled());
        setShowInMenu(false);
        setEvent(new EditorSelectAllEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ShowContextMenuEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.event.ShowContextMenuHandler#onShowContextMenu(org.exoplatform.ide.client.framework
     * .event.ShowContextMenuEvent) */
    @Override
    public void onShowContextMenu(ShowContextMenuEvent event) {
        boolean showInContextMenu = (event.getObject() instanceof Editor);
        setShowInContextMenu(showInContextMenu && isEditorPanelActive);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework
     * .ui.api.event.ViewActivatedEvent) */
    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        isEditorPanelActive = event.getView() instanceof EditorView;
        setShowInContextMenu(isEditorPanelActive);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        boolean isEnabled = (event.getFile() != null);
        setVisible(isEnabled);
        setEnabled(isEnabled);
    }
}
