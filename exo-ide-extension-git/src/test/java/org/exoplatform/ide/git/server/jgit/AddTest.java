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

import org.exoplatform.ide.git.shared.AddRequest;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AddTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class AddTest extends BaseTest {
    public void testNoAdd() throws Exception {
        File workDir = getDefaultRepository().getWorkTree();
        File file1 = addFile(workDir, "testNoAdd", CONTENT);
        checkNoFilesInCache(getDefaultRepository(), file1);
    }

    public void testUpdate() throws Exception {
        File workDir = getDefaultRepository().getWorkTree();
        File file1 = addFile(workDir, "testUpdate", CONTENT);
        AddRequest addRequest = new AddRequest();
        addRequest.setUpdate(true);
        getDefaultConnection().add(addRequest);
        // File not added in index. Existed file re-indexed if modified.
        checkNoFilesInCache(getDefaultRepository(), file1);
    }

    public void testAdd() throws Exception {
        File workDir = getDefaultRepository().getWorkTree();
        File file1 = addFile(workDir, "testAdd", CONTENT);
        AddRequest addRequest = new AddRequest();
        getDefaultConnection().add(addRequest);
        checkFilesInCache(getDefaultRepository(), file1);
    }

    public void testAddAfterRemove() throws Exception {
        File workDir = getDefaultRepository().getWorkTree();
        File readMe = new File(workDir, "README.txt");
        String relativePath = calculateRelativePath(workDir, readMe);
        checkFilesInCache(getDefaultRepository(), readMe);
        readMe.delete();
        AddRequest addRequest = new AddRequest();
        // If 'update' is 'true' then removed files should be removed from index.
        addRequest.setUpdate(true);
        getDefaultConnection().add(addRequest);
        checkNoFilesInCache(getDefaultRepository(), relativePath);
    }
}
