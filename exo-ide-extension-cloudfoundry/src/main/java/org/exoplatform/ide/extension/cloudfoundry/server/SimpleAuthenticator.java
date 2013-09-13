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
package org.exoplatform.ide.extension.cloudfoundry.server;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class SimpleAuthenticator extends CloudfoundryAuthenticator {
    private final String cfTarget;
    private final String cfUser;
    private final String cfPassword;

    public SimpleAuthenticator(String cfTarget, String cfUser, String cfPassword) {
        this.cfTarget = cfTarget;
        this.cfUser = cfUser;
        this.cfPassword = cfPassword;
    }

    @Override
    public String getEmail() {
        return cfUser;
    }

    @Override
    public String getPassword() {
        return cfPassword;
    }

    @Override
    public String getTarget() {
        return cfTarget;
    }
}
