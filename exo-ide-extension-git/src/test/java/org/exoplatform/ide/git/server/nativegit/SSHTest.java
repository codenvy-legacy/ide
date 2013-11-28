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
package org.exoplatform.ide.git.server.nativegit;

import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;


/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class SSHTest extends BaseTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.start();
    }

    @Test
    public void testSshKeysManager() throws Exception {
        SshKeyStore keyStore = (SshKeyStore) container.getComponentInstanceOfType(SshKeyStore.class);
        //generating key
        keyStore.genKeyPair("host.com", "comment", "password");
        //creating SshKeyManager with not default host
        SshKeysManager manager = new SshKeysManager(keyStore);
        //configuring where key should be saved.
        String key = manager.storeKeyIfNeed(DEFAULT_URI);
        assertArrayEquals(readFile(new File(key)).getBytes(),
                keyStore.getPrivateKey("host.com").getBytes());
        forClean.add(new File(key));
    }

    @Override
    protected void tearDown() throws Exception {
        container.stop();
        super.tearDown();
    }
}
