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
package org.exoplatform.ide.extension.aws.server.ec2;

import org.exoplatform.ide.extension.aws.shared.ec2.KeyPairInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class KeyPairInfoImpl implements KeyPairInfo {
    /** The name of the key pair. */
    private String name;

    /** The SHA-1 digest of the DER encoded private key. */
    private String fingerprint;

    public KeyPairInfoImpl(String keyName, String fingerprint) {
        this.name = keyName;
        this.fingerprint = fingerprint;
    }

    public KeyPairInfoImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public String toString() {
        return "KeyPairInfoImpl{" +
               "name='" + name + '\'' +
               ", fingerprint='" + fingerprint + '\'' +
               '}';
    }
}
