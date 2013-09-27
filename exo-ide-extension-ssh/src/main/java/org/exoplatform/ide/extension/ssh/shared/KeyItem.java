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
package org.exoplatform.ide.extension.ssh.shared;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class KeyItem {
    /** Host name. */
    private String host;

    /** URL for download public key. May be <code>null</code> if public key no available. */
    private String publicKeyURL;

    /** URL for remove key. */
    private String removeKeyURL;

    public KeyItem(String host, String publicKeyURL, String removeKeyURL) {
        this.host = host;
        this.publicKeyURL = publicKeyURL;
        this.removeKeyURL = removeKeyURL;
    }

    public KeyItem() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPublicKeyURL() {
        return publicKeyURL;
    }

    public void setPublicKeyURL(String publicKeyURL) {
        this.publicKeyURL = publicKeyURL;
    }

    public void setRemoveKeyURL(String removeKeyURL) {
        this.removeKeyURL = removeKeyURL;
    }

    public String getRemoveKeyURL() {
        return removeKeyURL;
    }
}
