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
package com.codenvy.ide.ext.git.client.branch;

import com.codenvy.ide.ext.git.client.BaseTest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_ALL;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Testing {@link BranchPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class BranchPresenterTest extends BaseTest {
    public static final String  BRANCH_NAME   = "branchName";
    public static final boolean NEED_DELETING = true;
    public static final boolean IS_REMOTE     = true;
    public static final boolean IS_ACTIVE     = true;
    @Mock
    private BranchView      view;
    @Mock
    private Branch          selectedBranch;
    @InjectMocks
    private BranchPresenter presenter;

    @Before
    public void disarm() {
        super.disarm();

        when(selectedBranch.getDisplayName()).thenReturn(BRANCH_NAME);
        when(selectedBranch.remote()).thenReturn(IS_REMOTE);
        when(selectedBranch.active()).thenReturn(IS_ACTIVE);
    }

    @Test
    public void testShowDialog() throws Exception {
        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testShowDialogWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service)
                .branchList(anyString(), anyString(), anyString(), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());

        presenter.showDialog();

        verify(resourceProvider).getActiveProject();
        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(DISABLE_BUTTON));
        verify(view).showDialog();

        verify(service).branchList(eq(VFS_ID), eq(PROJECT_ID), eq(LIST_ALL), (AsyncRequestCallback<JsonArray<Branch>>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    public void testOnCloseClicked() throws Exception {
        presenter.onCloseClicked();

        verify(view).close();
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (String name = Window.prompt(constant.branchTypeNew(), currentBranchName);)
    public void testOnRenameClicked() throws Exception {
        selectBranch();

        presenter.onRenameClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchRename(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), anyString(), (AsyncRequestCallback<String>)anyObject());
    }

    /** Select mock branch for testing. */
    private void selectBranch() {
        presenter.showDialog();
        presenter.onBranchSelected(selectedBranch);
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (boolean needToDelete = Window.confirm(constant.branchDeleteAsk(name));)
    public void testOnDeleteClicked() throws Exception {
        selectBranch();

        presenter.onDeleteClicked();

        verify(selectedBranch).getDisplayName();
        verify(service).branchDelete(eq(VFS_ID), eq(PROJECT_ID), anyString(), eq(NEED_DELETING), (AsyncRequestCallback<String>)anyObject());
    }

    @Test
    public void testOnCheckoutClicked() throws Exception {
        selectBranch();

        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).remote();
        verify(service).branchCheckout(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(console, never()).print(anyString());
    }

    @Test
    public void testOnCheckoutClickedWhenExceptionHappened() throws Exception {
        doThrow(RequestException.class).when(service).branchCheckout(anyString(), anyString(), anyString(), anyString(),
                                                                     anyBoolean(), (AsyncRequestCallback<String>)anyObject());

        selectBranch();

        presenter.onCheckoutClicked();

        verify(selectedBranch, times(2)).getDisplayName();
        verify(selectedBranch).remote();
        verify(service).branchCheckout(eq(VFS_ID), eq(PROJECT_ID), eq(BRANCH_NAME), eq(BRANCH_NAME), eq(IS_REMOTE),
                                       (AsyncRequestCallback<String>)anyObject());
        verify(console).print(anyString());
    }

    @Test
    @Ignore
    // Ignore this test because this method uses native method (String name = Window.prompt(constant.branchTypeNew(), "");)
    public void testOnCreateClicked() throws Exception {
        presenter.onCreateClicked();

        verify(service).branchCreate(eq(VFS_ID), eq(PROJECT_ID), anyString(), anyString(), (AsyncRequestCallback<Branch>)anyObject());
    }

    @Test
    public void testOnBranchSelected() throws Exception {
        presenter.onBranchSelected(selectedBranch);

        verify(view).setEnableCheckoutButton(eq(DISABLE_BUTTON));
        verify(view).setEnableDeleteButton(eq(!DISABLE_BUTTON));
        verify(view).setEnableRenameButton(eq(!DISABLE_BUTTON));
    }
}