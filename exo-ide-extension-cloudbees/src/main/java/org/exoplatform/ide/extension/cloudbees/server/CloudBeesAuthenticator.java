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
package org.exoplatform.ide.extension.cloudbees.server;

import com.cloudbees.api.AccountKeysResponse;
import com.cloudbees.api.BeesClient;

import org.exoplatform.ide.security.paas.Credential;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudBeesAuthenticator {
    public void login(BeesClient beesClient,
                      String domain,
                      String email,
                      String password,
                      Credential credential) throws Exception {
        AccountKeysResponse r = beesClient.accountKeys(domain, email, password);
        credential.setAttribute("api_key", r.getKey());
        credential.setAttribute("secret", r.getSecret());
    }

    public void login(BeesClient beesClient, Credential credential) throws Exception {
        login(beesClient, getDomain(), getEmail(), getPassword(), credential);
    }

    // For test.

    public String getEmail() {
        return null;
    }

    public String getPassword() {
        return null;
    }

    public String getDomain() {
        return null;
    }
}
