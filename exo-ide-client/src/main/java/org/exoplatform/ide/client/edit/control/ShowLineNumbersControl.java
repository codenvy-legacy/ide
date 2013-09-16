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
import org.exoplatform.ide.client.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class ShowLineNumbersControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                     ApplicationSettingsSavedHandler, ApplicationSettingsReceivedHandler {

    private static final String ID = "Edit/Show \\ Hide Line Numbers";

    private static final String TITLE_SHOW = IDE.IDE_LOCALIZATION_CONSTANT.showLineNumbersShowControl();

    private static final String TITLE_HIDE = IDE.IDE_LOCALIZATION_CONSTANT.showLineNumbersHideControl();

    private FileModel activeFile;

    private Editor activeEditor;

    private boolean showLineNumbers = true;

    /**
     *
     */
    public ShowLineNumbersControl() {
        super(ID);
        setTitle(TITLE_HIDE);
        setPrompt(TITLE_HIDE);
        setImages(IDEImageBundle.INSTANCE.hideLineNumbers(), IDEImageBundle.INSTANCE.hideLineNumbersDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsSavedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    /**
     *
     */
    private void updateState() {
        if (showLineNumbers) {
            // hide
            setTitle(TITLE_HIDE);
            setPrompt(TITLE_HIDE);
            setImages(IDEImageBundle.INSTANCE.hideLineNumbers(), IDEImageBundle.INSTANCE.hideLineNumbersDisabled());
            setEvent(new ShowLineNumbersEvent(false));
        } else {
            // show
            setTitle(TITLE_SHOW);
            setPrompt(TITLE_SHOW);
            setImages(IDEImageBundle.INSTANCE.showLineNumbers(), IDEImageBundle.INSTANCE.showLineNumbersDisabled());
            setEvent(new ShowLineNumbersEvent(true));
        }

        // verify and show
        if (activeFile == null || activeEditor == null || !activeEditor.isCapable(EditorCapability.SHOW_LINE_NUMBERS)) {
            setVisible(false);
            setEnabled(false);
        } else {
            setVisible(true);
            setEnabled(true);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeEditor = event.getEditor();
        activeFile = event.getFile();
        updateState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedHandler#onApplicationSettingsSaved(org
     *      .exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedEvent)
     */
    @Override
    public void onApplicationSettingsSaved(ApplicationSettingsSavedEvent event) {
        if (event.getApplicationSettings().getValueAsBoolean("line-numbers") != null) {
            showLineNumbers = event.getApplicationSettings().getValueAsBoolean("line-numbers");
        } else {
            showLineNumbers = true;
        }

        updateState();
    }

    /**
     * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     *      .exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
     */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        if (event.getApplicationSettings().getValueAsBoolean("line-numbers") != null) {
            showLineNumbers = event.getApplicationSettings().getValueAsBoolean("line-numbers");
        }

        updateState();
    }

}
