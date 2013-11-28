package org.exoplatform.ide.git.client.ssh;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handle SSH Key process events.
 */
public interface SSHKeyProcessorHandler extends EventHandler {
    /** When user performed to process ssh keys before making request ot Git service. */
    void onSSHKeyProcess(SSHKeyProcessorEvent event);
}
