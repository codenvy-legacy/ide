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
package org.exoplatform.ide.git.server.jgit.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.picocontainer.Startable;

/**
 * SSH session factory that use SshKeyProvider to get access to private keys. Factory does not support user interactivity (e.g. password
 * authentication).
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class IdeSshSessionFactory extends JschConfigSessionFactory implements Startable {
    private final SshKeyStore keyProvider;

    public IdeSshSessionFactory(SshKeyStore keyProvider) {
        this.keyProvider = keyProvider;
        init();
    }

    /** Initial this SshSessionFactory. By default turn off using "know-hosts" file. */
    protected void init() {
        JSch.setConfig("StrictHostKeyChecking", "no");
    }

    /**
     * @see org.eclipse.jgit.transport.JschConfigSessionFactory#configure(org.eclipse.jgit.transport.OpenSshConfig.Host,
     *      com.jcraft.jsch.Session)
     */
    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {
    }

    /**
     * @see org.eclipse.jgit.transport.JschConfigSessionFactory#getJSch(org.eclipse.jgit.transport.OpenSshConfig.Host,
     *      org.eclipse.jgit.util.FS)
     */
    @Override
    protected final JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
        try {
            String host = hc.getHostName();
            SshKey key = keyProvider.getPrivateKey(host);
            if (key == null) {
                throw new JSchException("SSH connection failed. Key file not found. ");
            }
            JSch jsch = new JSch();
            jsch.addIdentity(key.getIdentifier(), key.getBytes(), null, null);
            return jsch;
        } catch (SshKeyStoreException e) {
            throw new JSchException(e.getMessage(), e);
        }
    }

    /** @see org.picocontainer.Startable#start() */
    @Override
    public void start() {
        SshSessionFactory.setInstance(this);
    }

    /** @see org.picocontainer.Startable#stop() */
    @Override
    public void stop() {
        SshSessionFactory.setInstance(null);
    }
}
