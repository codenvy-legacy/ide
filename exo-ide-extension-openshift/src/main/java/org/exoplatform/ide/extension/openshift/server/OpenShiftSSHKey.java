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
package org.exoplatform.ide.extension.openshift.server;

import com.openshift.client.ISSHPublicKey;
import com.openshift.client.SSHKeyType;

import org.exoplatform.ide.extension.ssh.server.SshKey;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
class OpenShiftSSHKey implements ISSHPublicKey {
    private SshKey sshKey;

    OpenShiftSSHKey(SshKey sshKey) {
        this.sshKey = sshKey;
    }

    @Override
    public String getPublicKey() {
        return readSshKeyBody(sshKey);
    }

    @Override
    public SSHKeyType getKeyType() {
        return SSHKeyType.getByTypeId(readSshKeyType(sshKey));
    }

    private static String readSshKeyBody(SshKey sshKey) {
        byte[] b = sshKey.getBytes();
        StringBuilder sb = new StringBuilder();
        for (int i = 8 /* Skip "ssh-rsa " */; b[i] != ' ' && b[i] != '\n' && i < b.length; i++) {
            sb.append((char)b[i]);
        }
        return sb.toString();
    }

    private static String readSshKeyType(SshKey sshKey) {
        byte[] b = sshKey.getBytes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; b[i] != ' ' && i < b.length; i++) {
            sb.append((char)b[i]);
        }
        return sb.toString();
    }
}
