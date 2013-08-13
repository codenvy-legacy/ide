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
package com.codenvy.ide.ext.ssh.shared;

/**
 * Interface describe a request for generate a SSH-key.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GenKeyRequest.java Mar 21, 2012 6:14:27 PM azatsarynnyy $
 */
public interface GenKeyRequest {
    /**
     * Returns remote host name for which generate key.
     *
     * @return host name
     */
    String getHost();

    /**
     * Returns comment for public key.
     *
     * @return comment
     */
    String getComment();

    /**
     * Returns passphrase for private key.
     *
     * @return passphrase
     */
    String getPassphrase();
}