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
package org.exoplatform.ide.extension.googleappengine.client;


import com.codenvy.security.shared.Token;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: GaeTools.java Apr 18, 2013 vetal $
 */
public class GaeTools {

    public static final String APPENGINE_ADMIN_SCOPE = "https://www.googleapis.com/auth/appengine.admin";

    public static boolean isAuthenticatedInAppEngine(Token token) {
        if (token == null || token.getToken() == null || token.getToken().isEmpty())
            return false;
        if (token.getScope() == null || token.getScope().isEmpty())
            return false;
        return token.getScope().contains(APPENGINE_ADMIN_SCOPE);
    }


}
