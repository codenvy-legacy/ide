/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.action;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.actions.ShowHiddenFilesAction;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.event.RefreshProjectTreeEvent;
import org.eclipse.che.ide.api.project.tree.TreeSettings;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
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
        TreeStructure tree = mock(TreeStructure.class);
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
