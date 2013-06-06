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
import com.codenvy.ide.api.ui.action.ActionEvent;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ActionManagerImpl implements ActionManager {
    public static final  String                   ACTION_ELEMENT_NAME          = "action";
    public static final  String                   GROUP_ELEMENT_NAME           = "group";
    public static final  String                   ACTIONS_ELEMENT_NAME         = "actions";
    public static final  String                   CLASS_ATTR_NAME              = "class";
    public static final  String                   ID_ATTR_NAME                 = "id";
    public static final  String                   INTERNAL_ATTR_NAME           = "internal";
    public static final  String                   ICON_ATTR_NAME               = "icon";
    public static final  String                   ADD_TO_GROUP_ELEMENT_NAME    = "add-to-group";
    public static final  String                   SHORTCUT_ELEMENT_NAME        = "keyboard-shortcut";
    public static final  String                   MOUSE_SHORTCUT_ELEMENT_NAME  = "mouse-shortcut";
    public static final  String                   DESCRIPTION                  = "description";
    public static final  String                   TEXT_ATTR_NAME               = "text";
    public static final  String                   POPUP_ATTR_NAME              = "popup";
    public static final  String                   SEPARATOR_ELEMENT_NAME       = "separator";
    public static final  String                   REFERENCE_ELEMENT_NAME       = "reference";
    public static final  String                   GROUPID_ATTR_NAME            = "group-id";
    public static final  String                   ANCHOR_ELEMENT_NAME          = "anchor";
    public static final  String                   FIRST                        = "first";
    public static final  String                   LAST                         = "last";
    public static final  String                   BEFORE                       = "before";
    public static final  String                   AFTER                        = "after";
    public static final  String                   SECONDARY                    = "secondary";
    public static final  String                   RELATIVE_TO_ACTION_ATTR_NAME = "relative-to-action";
    public static final  String                   FIRST_KEYSTROKE_ATTR_NAME    = "first-keystroke";
    public static final  String                   SECOND_KEYSTROKE_ATTR_NAME   = "second-keystroke";
    public static final  String                   REMOVE_SHORTCUT_ATTR_NAME    = "remove";
    public static final  String                   REPLACE_SHORTCUT_ATTR_NAME   = "replace-all";
    public static final  String                   KEYMAP_ATTR_NAME             = "keymap";
    public static final  String                   KEYSTROKE_ATTR_NAME          = "keystroke";
    public static final  String                   REF_ATTR_NAME                = "ref";
    public static final  String                   ACTIONS_BUNDLE               = "messages.ActionsBundle";
    public static final  String                   USE_SHORTCUT_OF_ATTR_NAME    = "use-shortcut-of";
    public static final  String[]                 STRINGS                      = new String[0];
    public static final  String[]                 EMPTY_ARRAY                  = new String[0];
    public static final  String                   WINDOW_GROUP                 = "windowGroup";
    private static final int                      DEACTIVATED_TIMER_DELAY      = 5000;
    private static final int                      TIMER_DELAY                  = 500;
    private static final int                      UPDATE_DELAY_AFTER_TYPING    = 500;
    private final        Object                   myLock                       = new Object();
    private final        Map<String, Object>      myId2Action                  = new HashMap<String, Object>();
    private final        Map<String, Set<String>> myPlugin2Id                  = new HashMap<String, Set<String>>();
    private final        Map<String, Integer>     myId2Index                   = new HashMap<String, Integer>();
    private final        Map<Object, String>      myAction2Id                  = new HashMap<Object, String>();
    //    private final        List<String>              myNotRegisteredInternalActionIds = new ArrayList<String>();
//    private final        List<ActionListener>    myActionListeners                = new ArrayList<AnActionListener>();
    private final KeyBindingAgent myKeymapManager;
    //    private final DataManager   myDataManager;
//    private final List<ActionPopupMenuImpl> myPopups                    = new ArrayList<ActionPopupMenuImpl>();
//    private final Map<Action, DataContext>  myQueuedNotifications       = new LinkedHashMap<Action, DataContext>();
    private final Map<Action, ActionEvent> myQueuedNotificationsEvents = new LinkedHashMap<Action, ActionEvent>();
    //    private MyTimer myTimer;
    private int    myRegisteredActionsCount;
    private String myLastPreformedActionId;
    private String myPrevPerformedActionId;
    private long myLastTimeEditorWasTypedIn = 0;
    private Runnable myPreloadActionsRunnable;
    private boolean  myTransparentOnlyUpdate;
    private int myActionsPreloaded = 0;

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
    }

    public static boolean checkRelativeToAction(final String relativeToActionId,
                                                final Anchor anchor,
                                                final String actionName,
                                                final String pluginId) {
        if ((Anchor.BEFORE == anchor || Anchor.AFTER == anchor) && relativeToActionId == null) {
            reportActionError(pluginId, actionName + ": \"relative-to-action\" cannot be null if anchor is \"after\" or \"before\"");
            return false;
        }
        return true;
    }

    public static Anchor parseAnchor(final String anchorStr,
                                     final String actionName,
                                     final String pluginId) {
        if (anchorStr == null) {
            return Anchor.LAST;
        }

        if (FIRST.equalsIgnoreCase(anchorStr)) {
            return Anchor.FIRST;
        } else if (LAST.equalsIgnoreCase(anchorStr)) {
            return Anchor.LAST;
        } else if (BEFORE.equalsIgnoreCase(anchorStr)) {
            return Anchor.BEFORE;
        } else if (AFTER.equalsIgnoreCase(anchorStr)) {
            return Anchor.AFTER;
        } else {
            reportActionError(pluginId, actionName +
                                        ": anchor should be one of the following constants: \"first\", \"last\", \"before\" or \"after\"");
            return null;
        }
    }

    private static void assertActionIsGroupOrStub(final Action action) {
        if (!(action instanceof ActionGroup)) {
            Log.error(ActionManagerImpl.class, "Action : " + action + "; class: " + action.getClass());
        }
    }

    private static void reportActionError(final String pluginId, final String message) {
        if (pluginId == null) {
            Log.error(ActionManagerImpl.class, message);
        } else {
            Log.error(ActionManagerImpl.class, pluginId, message);
        }
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public Action getAction(String id) {
        return getActionImpl(id, false);
    }

    private Action getActionImpl(String id, boolean canReturnStub) {

        Action action = (Action)myId2Action.get(id);

        return action;

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


    public Action getActionOrStub(String id) {
        return getActionImpl(id, true);
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
        synchronized (myLock) {
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

    public String getComponentName() {
        return "ActionManager";
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

    public String getLastPreformedActionId() {
        return myLastPreformedActionId;
    }

    public String getPrevPreformedActionId() {
        return myPrevPerformedActionId;
    }

    public Set<String> getActionIds() {
        return new HashSet<String>(myId2Action.keySet());

    }

}
