/*
 * Copyright (C) 2013 eXo Platform SAS.
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

import static org.junit.Assert.assertEquals;
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
        actionManager = new ActionManagerImpl(agent);
    }

    @Test
    public void shouldRegister() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction(IdeActions.GROUP_MAIN_MENU, defaultActionGroup);
        Action action = actionManager.getAction(IdeActions.GROUP_MAIN_MENU);
        assertEquals(defaultActionGroup, action);
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

    @Test
    public void shouldProvideId() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction(IdeActions.GROUP_MAIN_MENU, defaultActionGroup);
        String id = actionManager.getId(defaultActionGroup);
        assertEquals(IdeActions.GROUP_MAIN_MENU, id);
    }

}
