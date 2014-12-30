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
package com.codenvy.ide.action;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.actions.ShowHiddenFilesAction;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** @author Artem Zatsarynnyy */
@RunWith(MockitoJUnitRunner.class)
public class ShowHiddenFilesActionTest {
    @Mock
    private CoreLocalizationConstant localizationConstant;
    @Mock
    private EventBus                 eventBus;
    @Mock
    private AnalyticsEventLogger     analyticsEventLogger;
    @Mock
    private AppContext               appContext;
    @InjectMocks
    private ShowHiddenFilesAction    action;
    @Mock
    private TreeSettings             treeSettings;

    @Before
    public void setUp() {
        AbstractTreeStructure tree = mock(AbstractTreeStructure.class);
        CurrentProject currentProject = mock(CurrentProject.class);
        when(tree.getSettings()).thenReturn(treeSettings);
        when(currentProject.getCurrentTree()).thenReturn(tree);
        when(appContext.getCurrentProject()).thenReturn(currentProject);
    }

    @Test
    public void shouldChangeSettingsAndRefreshTree() {
        action.actionPerformed(mock(ActionEvent.class));

        verify(treeSettings).setShowHiddenItems(anyBoolean());
        verify(eventBus).fireEvent(Matchers.<RefreshProjectTreeEvent>anyObject());
    }
}
