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
package com.codenvy.ide.action;

import com.codenvy.ide.actions.ActionManagerImpl;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionManagerTest {

    @Mock
    private KeyBindingAgent agent;
    private ActionManager   actionManager;

    @Before
    public void init() {
        actionManager = new ActionManagerImpl();
    }

    @Test
    public void shouldUnregister() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction(IdeActions.GROUP_MAIN_MENU, defaultActionGroup);
        actionManager.unregisterAction(IdeActions.GROUP_MAIN_MENU);
        Action action = actionManager.getAction(IdeActions.GROUP_MAIN_MENU);
        assertNull(action);
    }

    @Test
    public void testIsGroup() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction(IdeActions.GROUP_MAIN_MENU, defaultActionGroup);
        boolean isGroup = actionManager.isGroup(IdeActions.GROUP_MAIN_MENU);
        assertTrue(isGroup);
    }

}
