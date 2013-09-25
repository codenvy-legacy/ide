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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.ext.java.client.projectmodel.Package;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NewPackagePagePresenterTest extends WizardsBaseTest {

    @Mock
    private NewPackagePageView view;

    private NewPackagePagePresenter presenter;

    @Mock
    private SelectionAgent selectionAgent;

    @Before
    public void setUp() throws Exception {
        when(view.getPackageName()).thenReturn("client");
        presenter = new NewPackagePagePresenter(view, resourceProvider, selectionAgent);
        presenter.setUpdateDelegate(updateDelegate);

    }

    @Test
    public void testFlipToNext() throws Exception {
        assertThat(presenter.flipToNext()).isNull();
    }

    @Test
    public void testCanFinish() throws Exception {
        presenter.checkPackageName();
        verify(updateDelegate).updateControls();
        assertThat(presenter.canFinish()).isTrue();
    }

    @Test
    public void testIsCompleted() throws Exception {
        when(view.getPackageName()).thenReturn("public");
        presenter.checkPackageName();
        verify(updateDelegate).updateControls();
        assertThat(presenter.isCompleted()).isFalse();
    }

    @Test
    public void testGetNotice() throws Exception {
        when(view.getPackageName()).thenReturn("public");
        presenter.checkPackageName();
        assertThat(presenter.getNotice()).contains("is not a valid Java identifier");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGo() throws Exception {
        presenter.checkPackageName();
        presenter.doFinish();
        verify(project).createPackage(eq(sourceFolder), eq("client"), (AsyncCallback<Package>)any());
    }

}
