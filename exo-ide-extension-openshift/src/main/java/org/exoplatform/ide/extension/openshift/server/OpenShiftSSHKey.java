/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
