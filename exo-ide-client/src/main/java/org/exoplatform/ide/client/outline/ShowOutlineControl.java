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
package org.exoplatform.ide.client.outline;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.outline.OutlineDisplay;
import org.exoplatform.ide.client.framework.outline.ShowOutlineEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.editor.client.api.EditorCapability;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class ShowOutlineControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                 ViewClosedHandler, ViewOpenedHandler {

    public static final String ID = "View/Show \\ Hide Outline";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outlineTitleControl();

    public static final String PROMPT_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.outlinePromptShowControl();

    public static final String PROMPT_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.outlinePromptHideControl();

    /**
     * Count of opened Outline panels.
     */
    private int openedOutlinePanels = 0;

    /**
     * Creates instance of this {@link ShowOutlineControl}
     */
    public ShowOutlineControl() {
        super(ID);
        setTitle(TITLE);
        setImages(IDEImageBundle.INSTANCE.outline(), IDEImageBundle.INSTANCE.outlineDisabled());
        setEvent(new ShowOutlineEvent(true));
        setEnabled(true);
        setDelimiterBefore(true);
        setCanBeSelected(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client
     * .editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null || event.getEditor() == null) {
            setVisible(false);
            return;
        }

        boolean visible = event.getEditor().isCapable(EditorCapability.OUTLINE);
        setVisible(visible);
        if (visible) {
            update();
        }
    }

    /**
     * Updates the visibility and enabling state of this control.
     */
    private void update() {
        if (openedOutlinePanels > 0) {
            setSelected(true);
            setPrompt(PROMPT_HIDE);
            setEvent(new ShowOutlineEvent(false));
        } else {
            setSelected(false);
            setPrompt(PROMPT_SHOW);
            setEvent(new ShowOutlineEvent(true));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewOpenedEvent) */
    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof OutlineDisplay) {
            openedOutlinePanels++;
            update();
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof OutlineDisplay) {
            openedOutlinePanels--;
            update();
        }
    }

}
