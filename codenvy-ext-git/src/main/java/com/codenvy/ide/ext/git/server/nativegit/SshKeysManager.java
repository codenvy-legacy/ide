/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.ssh.server.SshKey;
import com.codenvy.ide.ext.ssh.server.SshKeyStore;
import com.codenvy.ide.ext.ssh.server.SshKeyStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Loads ssh keys into filesystem.
 *
 * @author Eugene Voevodin
 */
@Singleton
public class SshKeysManager {

    private static final Logger  LOG                        = LoggerFactory.getLogger(SshKeysManager.class);
    private static final Pattern SSH_URL                    = Pattern.compile("((((git|ssh)://)(([^\\\\/@:]+@)??)" +
                                                                              "[^\\\\/@:]+)|([^\\\\/@:]+@[^\\\\/@:]+)" +
                                                                              ")(:|/)[^\\\\@:]+");
    private static final String  DEFAULT_KEY_DIRECTORY_PATH = System.getProperty("java.io.tmpdir");
    private static final String  DEFAULT_KEY_NAME           = "identity";

    private static String      keyDirectoryPath; // TODO(GUICE): initialize
    private final  SshKeyStore keyProvider;
    private Set<SshKeyUploaderProvider> sshKeyUploaderProviders;

    @Inject
    public SshKeysManager(SshKeyStore keyProvider, Set<SshKeyUploaderProvider> sshKeyUploaderProviders) {
        this.keyProvider = keyProvider;
        this.sshKeyUploaderProviders = sshKeyUploaderProviders;
    }

    public static String getKeyDirectoryPath() throws GitException {
        return (keyDirectoryPath == null ? DEFAULT_KEY_DIRECTORY_PATH : keyDirectoryPath) + '/'
               + EnvironmentContext.getCurrent().getUser().getName();
    }

    /**
     * Stores ssh key into filesystem.
     *
     * @param uri
     *         link to resource
     * @return path to ssh key
     * @throws GitException
     */
    public String storeKeyIfNeed(String uri) throws GitException {
        for (SshKeyUploaderProvider sshKeyUploaderProvider : sshKeyUploaderProviders) {
            if (sshKeyUploaderProvider.match(uri) && sshKeyUploaderProvider.uploadKey()) {
                break;
            }
        }

        String host;
        if ((host = getHost(uri)) == null)
            return null;
        //create directories if need
        File keyDirectory = new File(getKeyDirectoryPath(), host);
        if (!keyDirectory.exists()) {
            keyDirectory.mkdirs();
        }
        //write key if it exists
        SshKey key;
        try {
            if ((key = keyProvider.getPrivateKey(host)) == null)
                return null;
        } catch (SshKeyStoreException e) {
            LOG.error("It is not possible to get ssh key for " + host, e);
            throw new GitException("Cant get ssh key for " + host);
        }

        File keyFile = new File(getKeyDirectoryPath() + '/' + host + '/' + DEFAULT_KEY_NAME);
        try (FileOutputStream fos = new FileOutputStream(keyFile)) {
            fos.write(key.getBytes());
        } catch (Exception e) {
            LOG.error("Cant store key", e);
            throw new GitException("Cant store ssh key");
        }
        //set perm to -r--r--r--
        keyFile.setReadOnly();
        //set perm to ----------
        keyFile.setReadable(false, false);
        //set perm to -r--------
        keyFile.setReadable(true, true);
        //set perm to -rw-------
        keyFile.setWritable(true, true);
        return keyFile.toString();
    }

    /**
     * Parses URL and get host from it, if it is possible
     *
     * @param url
     *         URL
     * @return host if it exists in URL or <code>null</code> if it doesn't.
     */
    private String getHost(String url) {
        if (SSH_URL.matcher(url).matches()) {
            int start;
            if ((start = url.indexOf("://")) != -1) {
                /*
                    Host between ("://" or "@") and (":" or "/")
                    for ssh or git Schema uri.
                    ssh://user@host.com/some/path
                    ssh://host.com/some/path
                    git://host.com/user/repo
                    can be with port
                    ssh://host.com:port/some/path
                 */
                int endPoint = url.lastIndexOf(":") != start ? url.lastIndexOf(":") : url.indexOf("/", start + 3);
                int startPoint = !url.contains("@") ? start + 3 : url.indexOf("@") + 1;
                return url.substring(startPoint, endPoint);
            } else {
                /*
                    Host between "@" and ":"
                    user@host.com:login/repo
                 */
                return url.substring(url.indexOf("@") + 1, url.indexOf(":"));
            }
        }
        return null;
    }
}
