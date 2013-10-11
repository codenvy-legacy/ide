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
package org.exoplatform.ide.git.server.jgit;

import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.commons.security.shared.Token;

import com.codenvy.factory.SimpleFactoryUrlFormat;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.exoplatform.services.security.ConversationState;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Provides credentials for jGit if uri is WSO2.
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class OAuthCredentialsProvider extends CredentialsProvider implements Startable {

    private static final Logger LOG = LoggerFactory.getLogger(OAuthCredentialsProvider.class);
    private final OAuthTokenProvider oauthTokenProvider;

    public OAuthCredentialsProvider(OAuthTokenProvider provider) {
        this.oauthTokenProvider = provider;
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    @Override
    public boolean supports(CredentialItem... items) {
        return true;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
        if (SimpleFactoryUrlFormat.WSO_2_URL_PATTERN.matcher(uri.toString()).matches()) {
            try {
                Token token = oauthTokenProvider.getToken("wso2", ConversationState.getCurrent().getIdentity().getUserId());
                if (token != null) {
                    for (CredentialItem i : items) {
                        if (i instanceof CredentialItem.Username) {
                            ((CredentialItem.Username) i).setValue(token.getToken());
                            continue;
                        }
                        if (i instanceof CredentialItem.Password) {
                            ((CredentialItem.Password) i).setValue("x-oauth-basic".toCharArray());
                            continue;
                        }
                        LOG.error("Unexpected item " + i.getClass().getName());
                        throw new UnsupportedCredentialItem(uri, i.getClass().getName());
                    }
                } else {
                    throw new JGitInternalException("not authorized");
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        return true;
    }

    @Override
    public void start() {
        CredentialsProvider.setDefault(this);
    }

    @Override
    public void stop() {
        //nothing to do
    }
}
