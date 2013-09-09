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
package org.exoplatform.ide.client.navigation.control;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.collaboration.CollaborationPropertiesUtil;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class SaveFileControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                              EditorFileContentChangedHandler, FileSavedHandler, VfsChangedHandler,
                                                              ApplicationSettingsReceivedHandler {

    /** ID of this control */
    public static final String ID = "File/Save";

    /** Title of this control */
    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.saveFileControl();

    /** Currently active file */
    private FileModel activeFile;

    /** Lock tokens */
    private Map<String, String> lockTokens;

    private Editor activeEditor;

    /** Creates a new instance of this control */
    public SaveFileControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setDelimiterBefore(true);
        setImages(IDEImageBundle.INSTANCE.save(), IDEImageBundle.INSTANCE.saveDisabled());
        setEvent(new SaveFileEvent());
        setIgnoreDisable(true);
        setHotKey("Ctrl+S");
        setGroupName(GroupNames.SAVE);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
        IDE.addHandler(FileSavedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        setVisible(event.getVfsInfo() != null);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        activeEditor = event.getEditor();

        if (activeFile == null) {
            setEnabled(false);
            return;
        }

        if (activeFile.isLocked()) {
            if (!lockTokens.containsKey(activeFile.getId())) {
                setEnabled(false);
                return;
            }
        }

        if (!activeFile.isPersisted()) {
            setEnabled(false);
        } else {
            // TODO isContentChanged
            if (activeFile.isContentChanged() /* || activeFile.isPropertiesChanged() */) {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorFileContentChangedEvent) */
    @Override
    public void onEditorFileContentChanged(EditorFileContentChangedEvent event) {
        if (CollaborationPropertiesUtil.isCollaborationEnabled(event.getFile().getProject()) && activeEditor instanceof CollabEditor && !MimeType.TEXT_HTML.equals(event.getFile().getMimeType())) {
            setEnabled(false);
            return;
        }

        if (event.getFile().isLocked()) {
            if (!lockTokens.containsKey(event.getFile().getId())) {
                setEnabled(false);
                return;
            }
        }
        if (!event.getFile().isPersisted()) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null) {
            event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }
        lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
    }

    /** @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event
     * .FileSavedEvent) */
    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (activeFile == null || event.getFile() == null) {
            setEnabled(false);
            return;
        }

        if (event.getFile().getId().equals(activeFile.getId())) {
            setEnabled(false);
        }
    }

}
