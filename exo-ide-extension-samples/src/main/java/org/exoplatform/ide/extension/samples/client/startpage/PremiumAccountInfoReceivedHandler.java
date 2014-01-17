package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for Premium user info received.
 * @author Vladyslav Zhukovskii
 */
public interface PremiumAccountInfoReceivedHandler extends EventHandler {
    void onPremiumAccountInfoReceived(PremiumAccountInfoReceivedEvent event);
}
