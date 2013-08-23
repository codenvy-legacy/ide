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

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Link;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewHTMLPresenter implements PreviewHTMLHandler, ViewClosedHandler, EditorActiveFileChangedHandler {

    public interface Display extends IsView {

        /**
         * Shows preview
         *
         * @param url
         */
        void showPreview(String url);

        /**
         * Sets is preview available
         *
         * @param available
         */
        void setPreviewAvailable(boolean available);

        void setMessage(String message);

    }

    private static final String PREVIEW_NOT_AVAILABLE_SAVE_FILE = org.exoplatform.ide.client.IDE.OPERATION_CONSTANT
                                                                                                .previewNotAvailableSaveFile();

    /** Instance of attached Display */
    private Display display;

    private FileModel activeFile;

    public PreviewHTMLPresenter() {
        IDE.addHandler(PreviewHTMLEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

        IDE.getInstance().addControl(new PreviewHTMLControl(), Docking.TOOLBAR_RIGHT);
    }

    /**
     * Do preview HTML file
     *
     * @see org.exoplatform.ide.client.preview.event.PreviewHTMLHandler#onPreviewHTMLFile(org.exoplatform.ide.client.preview.event
     * .PreviewHTMLEvent)
     */
    @Override
    public void onPreviewHTMLFile(PreviewHTMLEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView((View)display);
        }
        display.asView().setViewVisible();
        previewActiveFile();
    }

    private void previewActiveFile() {
        if (activeFile == null) {
            IDE.getInstance().closeView(display.asView().getId());
            return;
        }

        if (MimeType.TEXT_HTML.equals(activeFile.getMimeType())) {
            if (!activeFile.isPersisted()) {
                display.setPreviewAvailable(false);
                display.setMessage(PREVIEW_NOT_AVAILABLE_SAVE_FILE);
            } else {
                display.setPreviewAvailable(true);
                display.showPreview(activeFile.getLinkByRelation(Link.REL_CONTENT_BY_PATH).getHref());
            }
        } else {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /**
     * Handler of ViewClosed event. Clear display instance if closed view is Preview.
     *
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();

        if (display != null) {
            previewActiveFile();
        }
    }

}
