package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event which fired when IDE want to determine if user has at least one premium account.
 * @author vzhukovskii@codenvy.com
 */
public class RequestPremiumAccountInfoEvent extends GwtEvent<RequestPremiumAccountInfoHandler> {
    public static final Type<RequestPremiumAccountInfoHandler> TYPE = new Type<RequestPremiumAccountInfoHandler>();

    @Override
    public Type<RequestPremiumAccountInfoHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestPremiumAccountInfoHandler handler) {
        handler.onRequestPremiumAccountInfo(this);
    }
}
