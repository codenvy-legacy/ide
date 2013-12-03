package org.exoplatform.ide.git.client.editor;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler to perform checking all opened files in editors to update their content.
 */
public interface UpdateOpenedFilesHandler extends EventHandler {
    /** Perform update content in files. */
    void onUpdateOpenedFiles(UpdateOpenedFilesEvent event);
}
