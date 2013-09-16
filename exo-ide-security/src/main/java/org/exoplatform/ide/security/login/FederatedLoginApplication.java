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
package org.exoplatform.ide.security.login;

import org.exoplatform.ide.security.openid.OpenIDAuthenticationService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Deploys service required for OpenID and OAuth authentication.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FederatedLoginApplication extends Application {
    private final Set<Class<?>> classes;

    public FederatedLoginApplication() {
        classes = new HashSet<Class<?>>(2);
        classes.add(OpenIDAuthenticationService.class);
        //classes.add(OAuthAuthenticationService.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
