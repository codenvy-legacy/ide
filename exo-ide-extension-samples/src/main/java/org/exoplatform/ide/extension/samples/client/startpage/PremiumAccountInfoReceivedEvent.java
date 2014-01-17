package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event when premium user info received.
 * @author Vladyslav Zhukovskii
 */
public class PremiumAccountInfoReceivedEvent extends GwtEvent<PremiumAccountInfoReceivedHandler> {
    public static final Type<PremiumAccountInfoReceivedHandler> TYPE = new Type<PremiumAccountInfoReceivedHandler>();

    private boolean userHasPremiumAccount = false;

    public PremiumAccountInfoReceivedEvent(boolean userHasPremiumAccount) {
        this.userHasPremiumAccount = userHasPremiumAccount;
    }

    @Override
    public Type<PremiumAccountInfoReceivedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PremiumAccountInfoReceivedHandler handler) {
        handler.onPremiumAccountInfoReceived(this);
    }

    public boolean isUserHasPremiumAccount() {
        return userHasPremiumAccount;
    }
}
