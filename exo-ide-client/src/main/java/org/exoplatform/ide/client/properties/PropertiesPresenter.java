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
package org.exoplatform.ide.client.properties;

import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesPresenter implements EditorActiveFileChangedHandler, ShowPropertiesHandler, ViewClosedHandler,
                                            FileSavedHandler {

    public interface Display extends IsView {

        void showProperties(FileModel file);

    }

    private Display display;

    private FileModel file;

    public PropertiesPresenter() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ShowPropertiesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(FileSavedEvent.TYPE, this);

        IDE.getInstance().addControl(new ShowPropertiesControl(), Docking.TOOLBAR_RIGHT);
    }

    @Override
    public void onShowProperties(ShowPropertiesEvent event) {
        if (event.isShowProperties() && display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView((View)display);
            display.showProperties(file);
            return;
        }

        if (!event.isShowProperties() && display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    private void refreshProperties(FileModel file) {
        if (this.file == null) {
            return;
        }

        if (!file.getId().equals(this.file.getId())) {
            return;
        }

        this.file = file;

        if (display != null) {
            display.showProperties(file);
        }
    }

    // TODO: need rework according new VFS
    // public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
    // {
    // if (event.getItem() instanceof FileModel)
    // {
    // refreshProperties((FileModel)event.getItem());
    // }
    // }

    // public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
    // {
    // if (event.getItem() instanceof File)
    // {
    // refreshProperties((File)event.getItem());
    // }
    // }

    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        file = event.getFile();
        if (display != null) {
            if (file == null) {
                IDE.getInstance().closeView(display.asView().getId());
            } else {
                display.showProperties(file);
            }
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event
     *      .FileSavedEvent)
     */
    @Override
    public void onFileSaved(FileSavedEvent event) {
        refreshProperties(event.getFile());
    }

}
