/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.client.projectExplorer;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView.ActionDelegate;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 *         Aug 3, 2012
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectExplorerPresenterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ProjectExplorerView projectExplorerView;

    @Spy
    EventBus eventBus = new SimpleEventBus();

    @Mock
    CoreLocalizationConstant coreLocalizationConstant;

    @InjectMocks
    ProjectExplorerPartPresenter explorerPresenter;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();
    }

    @After
    public void restore() {
        //
        GWTMockUtilities.restore();
    }

    /** Check that Presenter binds a double click handler */
    @Test
    public void shouldBindDoubleClickHandler() {

        verify(projectExplorerView).setDelegate((ActionDelegate)any());
    }

    /** Testing that doubleClick generates OpenFileEvent and fire it on EventBus */
    @Test
    public void shouldFireOpenFileOnDoubleClick() {
        // TODO
    }

    @Test
    public void shouldRetrieveFileListOnGo() {
        // TODO
    }
}
