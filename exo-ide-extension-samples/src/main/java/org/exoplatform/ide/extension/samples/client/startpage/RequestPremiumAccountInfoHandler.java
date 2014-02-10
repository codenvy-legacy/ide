package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handle to perform action when IDE want to determine if user has at least one premium account.
 * @author vzhukovskii@codenvy.com
 */
public interface RequestPremiumAccountInfoHandler extends EventHandler {
    void onRequestPremiumAccountInfo(RequestPremiumAccountInfoEvent event);
}
