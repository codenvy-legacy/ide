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

    @Inject
    public SshKeysManager(SshKeyStore keyProvider) {
        this.keyProvider = keyProvider;
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
