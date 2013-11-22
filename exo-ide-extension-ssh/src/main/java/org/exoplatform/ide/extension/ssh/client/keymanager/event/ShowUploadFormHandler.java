package org.exoplatform.ide.extension.ssh.client.keymanager.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for event showing upload key form.
 */
public interface ShowUploadFormHandler extends EventHandler {
    void onShowSshKeyUploadForm(ShowUploadFormEvent event);
}
