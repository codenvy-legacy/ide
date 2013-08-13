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
package com.codenvy.ide.client.projectExplorer;

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
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Aug 3, 2012
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectExpolorerPresenterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ProjectExplorerView projectExplorerView;

    @Spy
    EventBus eventBus = new SimpleEventBus();

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
    public void shoudFireOpenFileOnDoubleClick() {
        // TODO
    }

    @Test
    public void shouldRetrieveFileListOnGo() {
        // TODO
    }
}
