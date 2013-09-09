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
package org.exoplatform.ide.client.preview;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class PreviewHTMLControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                 FileSavedHandler {

    public static final String ID = "Run/Show Preview";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.htmlPreview();

    private FileModel currentlyActiveFile;

    /**
     *
     */
    public PreviewHTMLControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.preview(), IDEImageBundle.INSTANCE.previewDisabled());
        setEvent(new PreviewHTMLEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(FileSavedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        currentlyActiveFile = event.getFile();
        updateVisibility(currentlyActiveFile, currentlyActiveFile == null ? false : !currentlyActiveFile.isPersisted());
    }

    /**
     * @param file
     * @param isNew
     */
    private void updateVisibility(FileModel file, boolean isNew) {
        if (file == null) {
            setVisible(false);
            setEnabled(false);
            return;
        }

        if (MimeType.TEXT_HTML.equals(file.getMimeType())) {
            setVisible(true);
            if (isNew) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        } else {
            setVisible(false);
            setEnabled(false);
        }
    }

    /** @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event
     * .FileSavedEvent) */
    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (currentlyActiveFile != null && event.getFile().getId().equals(currentlyActiveFile.getId())) {
            updateVisibility(currentlyActiveFile, false);
        }
    }

}
