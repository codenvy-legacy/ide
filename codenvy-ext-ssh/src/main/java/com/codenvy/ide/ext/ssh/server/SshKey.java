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
package com.codenvy.ide.ext.ssh.server;

/**
 * SSH key.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SshKey {
    private final String identifier;
    private final byte[] bytes;

    public SshKey(String identifier, byte[] bytes) {
        this.identifier = identifier;
        this.bytes = bytes;
    }

    /**
     * Identifier of SSH key, e.g. path to file where key stored, etc.
     *
     * @return identifier of key file
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get SSH key as byte array.
     *
     * @return SSH key as byte array
     */
    public byte[] getBytes() {
        byte[] copy = new byte[bytes.length];
        System.arraycopy(bytes, 0, copy, 0, copy.length);
        return copy;
    }
}
