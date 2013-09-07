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
package com.codenvy.ide.ext.git.client.delete;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.rest.AsyncRequestCallback;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link DeleteRepositoryPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class DeleteRepositoryPresenterTest extends BaseTest {
    @InjectMocks
    private DeleteRepositoryPresenter presenter;

    @Test
    @Ignore
    // Ignore this test because this method uses native method
    // (boolean needToDelete = Window.confirm(constant.deleteGitRepositoryQuestion(repository));)
    public void testDeleteRepository() throws Exception {
        presenter.deleteRepository();

        verify(resourceProvider).getActiveProject();
        verify(project).getPath();

        verify(service).deleteRepository(eq(VFS_ID), eq(PROJECT_ID), (AsyncRequestCallback<Void>)anyObject());
    }
}