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
package com.codenvy.ide.ext.git.client.commit;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.websocket.rest.RequestCallback;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CommitPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CommitPresenterTest extends BaseTest {
    public static final boolean ALL_FILE_INCLUDES = true;
    public static final boolean IS_OVERWRITTEN    = true;
    public static final String  COMMIT_TEXT       = "commit text";
    @Mock
    private CommitView      view;
    @InjectMocks
    private CommitPresenter presenter;

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(view).setAmend(eq(!IS_OVERWRITTEN));
        verify(view).setAllFilesInclude(eq(!ALL_FILE_INCLUDES));
        verify(view).setMessage(EMPTY_TEXT);
        verify(view).focusInMessageField();
        verify(view).setEnableCommitButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (DtoClientImpls.RevisionImpl revision = DtoClientImpls.RevisionImpl.make();)
    public void testOnCommitClickedWebsocketRequest() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);
        when(view.isAllFilesInclued()).thenReturn(ALL_FILE_INCLUDES);
        when(view.isAmend()).thenReturn(IS_OVERWRITTEN);

        presenter.onCommitClicked();

        verify(view).getMessage();
        verify(view).isAllFilesInclued();
        verify(view).isAmend();
        verify(view).close();

        verify(service).commitWS(eq(VFS_ID), eq(project), eq(COMMIT_TEXT), eq(ALL_FILE_INCLUDES), eq(IS_OVERWRITTEN),
                                 (RequestCallback<Revision>)anyObject());
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnValueChangedWhenCommitMessageEmpty() throws Exception {
        when(view.getMessage()).thenReturn(EMPTY_TEXT);

        presenter.onValueChanged();

        verify(view).setEnableCommitButton(eq(DISABLE_BUTTON));
    }

    @Test
    public void testOnValueChanged() throws Exception {
        when(view.getMessage()).thenReturn(COMMIT_TEXT);

        presenter.onValueChanged();

        verify(view).setEnableCommitButton(eq(!DISABLE_BUTTON));
    }
}