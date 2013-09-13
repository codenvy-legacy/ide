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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Store list of user federated login ,e.g. OpenID, OAuth, etc. Pair userId|password (temporary generated usually)
 * added in this list by suitable service for federated login. After adding userId|password in this list login service
 * should redirect user ot protected area where LoginModule may be used for checking userId|password over method {@link
 * #contains(String, String)}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class FederatedLoginList {
    public static final Permission LOGIN_LIST_PERMISSION = new RuntimePermission("federatedLoginList");

    private final Set<String> store = new CopyOnWriteArraySet<String>();

    public void add(String userId, String password) {
        checkPermission();
        store.add(digest(userId, password));
    }

    public boolean contains(String userId, String password) {
        checkPermission();
        return store.contains(digest(userId, password));
    }

    public void remove(String userId, String password) {
        checkPermission();
        store.remove(digest(userId, password));
    }

    private void checkPermission() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(LOGIN_LIST_PERMISSION);
        }
    }

    private String digest(String userId, String password) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        final String src = userId + ':' + password;
        byte[] result = messageDigest.digest(src.getBytes());
        StringBuilder buf = new StringBuilder();
        for (byte b : result) {
            buf.append(HEX[(b >> 4) & 0x0f]);
            buf.append(HEX[b & 0x0f]);
        }
        return buf.toString();
    }

    private static final char[] HEX =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
