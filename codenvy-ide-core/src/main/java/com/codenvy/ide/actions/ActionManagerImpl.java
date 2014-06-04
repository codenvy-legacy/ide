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
package com.codenvy.ide.actions;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** @author Evgen Vidolob */
public class ActionManagerImpl implements ActionManager {

    public static final String[]                 EMPTY_ARRAY = new String[0];
    private final       Map<String, Object>      myId2Action = new HashMap<>();
    private final       Map<String, Set<String>> myPlugin2Id = new HashMap<>();
    private final       Map<String, Integer>     myId2Index  = new HashMap<>();
    private final       Map<Object, String>      myAction2Id = new HashMap<>();
    private int myRegisteredActionsCount;

    @Inject
    public ActionManagerImpl() {
        registerDefaultActionGroups();
    }

    private void registerDefaultActionGroups() {
        // register default action groups for main menu
        DefaultActionGroup mainMenu = new DefaultActionGroup(this);
        registerAction(IdeActions.GROUP_MAIN_MENU, mainMenu);

        DefaultActionGroup fileGroup = new DefaultActionGroup("File", true, this);
        registerAction(IdeActions.GROUP_FILE, fileGroup);
        mainMenu.add(fileGroup);

        DefaultActionGroup codeGroup = new DefaultActionGroup("Code", true, this);
        registerAction(IdeActions.GROUP_CODE, codeGroup);
        Constraints afterFile = new Constraints(Anchor.AFTER, IdeActions.GROUP_FILE);
        mainMenu.add(codeGroup, afterFile);

        DefaultActionGroup buildGroup = new DefaultActionGroup("Build", true, this);
        registerAction(IdeActions.GROUP_BUILD, buildGroup);
        Constraints afterCode = new Constraints(Anchor.AFTER, IdeActions.GROUP_CODE);
        mainMenu.add(buildGroup, afterCode);

        DefaultActionGroup runGroup = new DefaultActionGroup("Run", true, this);
        registerAction(IdeActions.GROUP_RUN, runGroup);
        Constraints afterBuild = new Constraints(Anchor.AFTER, IdeActions.GROUP_BUILD);
        mainMenu.add(runGroup, afterBuild);

        DefaultActionGroup windowGroup = new DefaultActionGroup("Window", true, this);
        registerAction(IdeActions.GROUP_WINDOW, windowGroup);
        mainMenu.add(windowGroup);

        DefaultActionGroup helpGroup = new DefaultActionGroup("Help", true, this);
        registerAction(IdeActions.GROUP_HELP, helpGroup);
        Constraints afterWindow = new Constraints(Anchor.AFTER, IdeActions.GROUP_WINDOW);
        mainMenu.add(helpGroup, afterWindow);


        // register default action groups for context menu
        DefaultActionGroup mainContextMenuGroup = new DefaultActionGroup(IdeActions.GROUP_MAIN_CONTEXT_MENU, false, this);
        registerAction(IdeActions.GROUP_MAIN_CONTEXT_MENU, mainContextMenuGroup);

        DefaultActionGroup buildContextMenuGroup = new DefaultActionGroup(IdeActions.GROUP_BUILD_CONTEXT_MENU, false, this);
        registerAction(IdeActions.GROUP_BUILD_CONTEXT_MENU, buildContextMenuGroup);

        DefaultActionGroup runContextMenuGroup = new DefaultActionGroup(IdeActions.GROUP_RUN_CONTEXT_MENU, false, this);
        registerAction(IdeActions.GROUP_RUN_CONTEXT_MENU, runContextMenuGroup);


        // register default action groups for main toolbar
        DefaultActionGroup mainToolbarGroup = new DefaultActionGroup(this);
        registerAction(IdeActions.GROUP_MAIN_TOOLBAR, mainToolbarGroup);
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
        ArrayList<String> idList = new ArrayList<>();
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
                pluginActionIds = new HashSet<>();
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
        return new HashSet<>(myId2Action.keySet());
    }

}
