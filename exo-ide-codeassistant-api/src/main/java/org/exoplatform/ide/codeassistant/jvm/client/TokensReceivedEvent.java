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

package org.exoplatform.ide.codeassistant.jvm.client;

import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TokensReceivedEvent extends GwtEvent<TokensReceivedHandler> {

    public static final GwtEvent.Type<TokensReceivedHandler> TYPE = new GwtEvent.Type<TokensReceivedHandler>();

    private String providerId;

    private List<? extends Token> tokens;

    public TokensReceivedEvent(String providerId, List<? extends Token> tokens) {
        this.providerId = providerId;
        this.tokens = tokens;
    }

    public String getProviderId() {
        return providerId;
    }

    public List<? extends Token> getTokens() {
        return tokens;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<TokensReceivedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TokensReceivedHandler handler) {
        handler.onTokensReceived(this);
    }

}
