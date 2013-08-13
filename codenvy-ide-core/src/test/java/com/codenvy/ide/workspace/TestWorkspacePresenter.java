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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.part.PartStackPresenter;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link WorkspacePresenter} functionality.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestWorkspacePresenter {
    /**
     *
     */
    private static final PartStackType TYPE = PartStackType.EDITING;

    @Mock
    Provider<PartStackPresenter> partStackProvider;

    @Mock
    Provider<WorkBenchPresenter> activePerspectiveProvider;

    @Mock
    WorkBenchPresenter activePerspective;

    @Mock
    WorkspaceView view;

    @Mock
    PartStackPresenter partStack;

    WorkspacePresenter presenter;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        when(partStackProvider.get()).thenReturn(partStack);
        when(activePerspectiveProvider.get()).thenReturn(activePerspective);

        presenter = new WorkspacePresenter(view, null, null, activePerspectiveProvider);
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    @Test
    public void shouldAddToStack() {
        PartPresenter part = mock(PartPresenter.class);
        presenter.openPart(part, TYPE);
        // verify part added to proper stack
        verify(activePerspective).openPart(eq(part), eq(TYPE));
    }

    @Test
    public void shouldCallSetActivePart() {
        PartPresenter part = mock(PartPresenter.class);
        presenter.setActivePart(part);

        verify(activePerspective).setActivePart(eq(part));
    }
}