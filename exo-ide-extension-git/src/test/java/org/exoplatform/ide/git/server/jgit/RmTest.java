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
package org.exoplatform.ide.git.server.jgit;

import org.exoplatform.ide.git.shared.RmRequest;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RmTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class RmTest extends BaseTest {
    public void testRmNotCached() throws Exception {
        RmRequest req = new RmRequest(new String[]{"README.txt"});
        req.setCached(false);
        getDefaultConnection().rm(req);
        assertFalse(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
        checkNoFilesInCache(getDefaultRepository(), "README.txt");
    }

    public void testRmCached() throws Exception {
        RmRequest req = new RmRequest(new String[]{"README.txt"});
        req.setCached(true);
        getDefaultConnection().rm(req);
        assertTrue(new File(getDefaultRepository().getWorkTree(), "README.txt").exists());
        checkNoFilesInCache(getDefaultRepository(), "README.txt");
    }
}
