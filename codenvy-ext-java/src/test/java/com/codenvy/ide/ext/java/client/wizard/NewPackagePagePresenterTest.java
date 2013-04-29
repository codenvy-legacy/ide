/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
