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
package com.codenvy.ide.actions;


import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ActionManagerImpl implements ActionManager {

    public static final String[]                 EMPTY_ARRAY = new String[0];
    private final       Map<String, Object>      myId2Action = new HashMap<String, Object>();
    private final       Map<String, Set<String>> myPlugin2Id = new HashMap<String, Set<String>>();
    private final       Map<String, Integer>     myId2Index  = new HashMap<String, Integer>();
    private final       Map<Object, String>      myAction2Id = new HashMap<Object, String>();
    private final KeyBindingAgent myKeymapManager;
    private       int             myRegisteredActionsCount;

    @Inject
    public ActionManagerImpl(KeyBindingAgent keymapManager) {
        myKeymapManager = keymapManager;
        registerDefaultActionGroups();
    }

    private void registerDefaultActionGroups() {
        DefaultActionGroup mainMenu = new DefaultActionGroup(this);
        registerAction(IdeActions.GROUP_MAIN_MENU, mainMenu);
        DefaultActionGroup fileGroup = new DefaultActionGroup("File", true, this);
        registerAction(IdeActions.GROUP_FILE, fileGroup);
        mainMenu.add(fileGroup);

        DefaultActionGroup window = new DefaultActionGroup("Window", true, this);
        registerAction(IdeActions.GROUP_WINDOW, window);
        mainMenu.add(window);

        DefaultActionGroup project = new DefaultActionGroup("Project", true, this);
        registerAction(IdeActions.GROUP_PROJECT, project);
        Constraints afterFile = new Constraints(Anchor.AFTER, IdeActions.GROUP_FILE);
        mainMenu.add(project, afterFile);

        DefaultActionGroup paas = new DefaultActionGroup("Paas", true, this);
        registerAction(IdeActions.GROUP_PAAS, paas);
        Constraints beforeWindow = new Constraints(Anchor.BEFORE, IdeActions.GROUP_WINDOW);
        mainMenu.add(paas, beforeWindow);

        DefaultActionGroup projectPaas = new DefaultActionGroup("Paas", true, this);
        registerAction(IdeActions.GROUP_PROJECT_PAAS, projectPaas);
        project.add(projectPaas);

        DefaultActionGroup runGroup = new DefaultActionGroup("Run", true, this);
        registerAction(IdeActions.GROUP_RUN, runGroup);
        Constraints afterProject = new Constraints(Anchor.AFTER, IdeActions.GROUP_PROJECT);
        mainMenu.add(runGroup, afterProject);
    }

    private static void reportActionError(final String pluginId, final String message) {
        if (pluginId == null) {
            Log.error(ActionManagerImpl.class, message);
        } else {
            Log.error(ActionManagerImpl.class, pluginId, message);
        }
    }

    public Action getAction(String id) {
        return getActionImpl(id, false);
    }

    private Action getActionImpl(String id, boolean canReturnStub) {

        return (Action)myId2Action.get(id);

    }


    public String getId(Action action) {
        return myAction2Id.get(action);
    }

    public String[] getActionIds(String idPrefix) {

        ArrayList<String> idList = new ArrayList<String>();
        for (String id : myId2Action.keySet()) {
            if (id.startsWith(idPrefix)) {
                idList.add(id);
            }
        }
        return idList.toArray(new String[idList.size()]);

    }

    public boolean isGroup(String actionId) {
        return getActionImpl(actionId, true) instanceof ActionGroup;
    }


    public Action getParentGroup(final String groupId,
                                 final String actionName,
                                 final String pluginId) {
        if (groupId == null || groupId.length() == 0) {
            reportActionError(pluginId, actionName + ": attribute \"group-id\" should be defined");
            return null;
        }
        Action parentGroup = getActionImpl(groupId, true);
        if (parentGroup == null) {
            reportActionError(pluginId, actionName + ": group with id \"" + groupId +
                                        "\" isn't registered; action will be added to the \"Other\" group");
            parentGroup = getActionImpl(IdeActions.GROUP_OTHER_MENU, true);
        }
        if (!(parentGroup instanceof DefaultActionGroup)) {
            reportActionError(pluginId, actionName + ": group with id \"" + groupId + "\" should be instance of " +
                                        DefaultActionGroup.class.getName() +
                                        " but was " + parentGroup.getClass());
            return null;
        }
        return parentGroup;
    }

    public void registerAction(String actionId, Action action, String pluginId) {

        if (myId2Action.containsKey(actionId)) {
            reportActionError(pluginId, "action with the ID \"" + actionId + "\" was already registered. Action being registered is " +
                                        action.toString() +
                                        "; Registered action is " +
                                        myId2Action.get(actionId) + pluginId);
            return;
        }
        if (myAction2Id.containsKey(action)) {
            reportActionError(pluginId, "action was already registered for another ID. ID is " + myAction2Id.get(action) +
                                        pluginId);
            return;
        }
        myId2Action.put(actionId, action);
        myId2Index.put(actionId, myRegisteredActionsCount++);
        myAction2Id.put(action, actionId);
        if (pluginId != null && !(action instanceof ActionGroup)) {
            Set<String> pluginActionIds = myPlugin2Id.get(pluginId);
            if (pluginActionIds == null) {
                pluginActionIds = new HashSet<String>();
                myPlugin2Id.put(pluginId, pluginActionIds);
            }
            pluginActionIds.add(actionId);
        }
//            action.registerCustomShortcutSet(new ProxyShortcutSet(actionId, myKeymapManager), null);

    }

    public void registerAction(String actionId, Action action) {
        registerAction(actionId, action, null);
    }

    public void unregisterAction(String actionId) {

        if (!myId2Action.containsKey(actionId)) {

            Log.debug(getClass(), "action with ID " + actionId + " wasn't registered");
            return;
        }
        Action oldValue = (Action)myId2Action.remove(actionId);
        myAction2Id.remove(oldValue);
        myId2Index.remove(actionId);
        for (String pluginName : myPlugin2Id.keySet()) {
            final Set<String> pluginActions = myPlugin2Id.get(pluginName);
            if (pluginActions != null) {
                pluginActions.remove(actionId);
            }
        }

    }

    public Comparator<String> getRegistrationOrderComparator() {
        return new Comparator<String>() {
            public int compare(String id1, String id2) {
                return myId2Index.get(id1) - myId2Index.get(id2);
            }
        };
    }

    public String[] getPluginActions(String pluginName) {
        if (myPlugin2Id.containsKey(pluginName)) {
            final Set<String> pluginActions = myPlugin2Id.get(pluginName);
            return pluginActions.toArray(new String[pluginActions.size()]);
        }
        return EMPTY_ARRAY;
    }

    public Set<String> getActionIds() {
        return new HashSet<String>(myId2Action.keySet());

    }

}
