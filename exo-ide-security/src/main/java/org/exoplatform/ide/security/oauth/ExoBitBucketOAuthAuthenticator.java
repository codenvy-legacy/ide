package org.exoplatform.ide.security.oauth;

import com.codenvy.security.oauth.oauth1.BitBucketOAuthAuthenticator;

import org.exoplatform.container.xml.InitParams;

import static org.exoplatform.ide.security.oauth.ExoGoogleOAuthAuthenticator.createClientSecrets;

/** BitBucketOAuthAuthenticator configured over eXo container. */
public class ExoBitBucketOAuthAuthenticator extends BitBucketOAuthAuthenticator {
    public ExoBitBucketOAuthAuthenticator(InitParams initParams) {
        super(createClientSecrets(initParams));
    }
}
