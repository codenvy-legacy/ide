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
//        myDataManager = dataManager;

//        registerPluginActions();
    }


//    private static ResourceBundle getActionsResourceBundle(ClassLoader loader, IdeaPluginDescriptor plugin) {
//        final String resBundleName =
//                plugin != null && !plugin.getPluginId().getIdString().equals("com.intellij") ? plugin.getResourceBundleBaseName()
//                                                                                             : ACTIONS_BUNDLE;
//        ResourceBundle bundle = null;
//        if (resBundleName != null) {
//            bundle = AbstractBundle.getResourceBundle(resBundleName, loader);
//        }
//        return bundle;
//    }
//
//    public ActionPopupMenu createActionPopupMenu(String place,  ActionGroup group,
//                                                  PresentationFactory presentationFactory) {
//        return new ActionPopupMenuImpl(place, group, this, presentationFactory);
//    }
//
//    public ActionPopupMenu createActionPopupMenu(String place,  ActionGroup group) {
//        return new ActionPopupMenuImpl(place, group, this, null);
//    }
//
//    public ActionToolbar createActionToolbar(final String place, final ActionGroup group, final boolean horizontal) {
//        return createActionToolbar(place, group, horizontal, false);
//    }
//
//    public ActionToolbar createActionToolbar(final String place, final ActionGroup group, final boolean horizontal,
// final boolean decorateButtons) {
//        return new ActionToolbarImpl(place, group, horizontal, decorateButtons, myDataManager, this, (KeymapManagerEx)myKeymapManager);
//    }
//
//
//    private static boolean isSecondary(Element element) {
//        return "true".equalsIgnoreCase(element.getAttributeValue(SECONDARY));
//    }

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

//    private static void processMouseShortcutNode(Element element, String actionId, PluginId pluginId) {
//        String keystrokeString = element.getAttributeValue(KEYSTROKE_ATTR_NAME);
//        if (keystrokeString == null || keystrokeString.trim().length() == 0) {
//            reportActionError(pluginId, "\"keystroke\" attribute must be specified for action with id=" + actionId);
//            return;
//        }
//        MouseShortcut shortcut;
//        try {
//            shortcut = KeymapUtil.parseMouseShortcut(keystrokeString);
//        } catch (Exception ex) {
//            reportActionError(pluginId, "\"keystroke\" attribute has invalid value for action with id=" + actionId);
//            return;
//        }
//
//        String keymapName = element.getAttributeValue(KEYMAP_ATTR_NAME);
//        if (keymapName == null || keymapName.length() == 0) {
//            reportActionError(pluginId, "attribute \"keymap\" should be defined");
//            return;
//        }
//        Keymap keymap = KeymapManager.getInstance().getKeymap(keymapName);
//        if (keymap == null) {
//            reportActionError(pluginId, "keymap \"" + keymapName + "\" not found");
//            return;
//        }
//
//        final String removeOption = element.getAttributeValue(REMOVE_SHORTCUT_ATTR_NAME);
//        if (Boolean.valueOf(removeOption)) {
//            keymap.removeShortcut(actionId, shortcut);
//        } else {
//            keymap.addShortcut(actionId, shortcut);
//        }
//    }

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

//    private static String getPluginInfo( PluginId id) {
//        if (id != null) {
//            final IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);
//            if (plugin != null) {
//                String name = plugin.getName();
//                if (name == null) {
//                    name = id.getIdString();
//                }
//                return " Plugin: " + name;
//            }
//        }
//        return "";
//    }

    public void initComponent() {
    }

    public void disposeComponent() {
//        if (myTimer != null) {
//            myTimer.stop();
//            myTimer = null;
//        }
    }

    public Action getAction(String id) {
        return getActionImpl(id, false);
    }

    private Action getActionImpl(String id, boolean canReturnStub) {

        Action action = (Action)myId2Action.get(id);

        return action;

    }

//    /** Converts action's stub to normal action. */
//    private AnAction convert(ActionStub stub) {
//        LOG.assertTrue(myAction2Id.containsKey(stub));
//        myAction2Id.remove(stub);
//
//        LOG.assertTrue(myId2Action.containsKey(stub.getId()));
//
//        AnAction action = (AnAction)myId2Action.remove(stub.getId());
//        LOG.assertTrue(action != null);
//        LOG.assertTrue(action.equals(stub));
//
//        Object obj;
//        String className = stub.getClassName();
//        try {
//            Constructor<?> constructor = Class.forName(className, true, stub.getLoader()).getDeclaredConstructor();
//            constructor.setAccessible(true);
//            obj = constructor.newInstance();
//        } catch (ClassNotFoundException e) {
//            PluginId pluginId = stub.getPluginId();
//            if (pluginId != null) {
//                throw new PluginException("class with name \"" + className + "\" not found", e, pluginId);
//            } else {
//                throw new IllegalStateException("class with name \"" + className + "\" not found");
//            }
//        } catch (UnsupportedClassVersionError e) {
//            PluginId pluginId = stub.getPluginId();
//            if (pluginId != null) {
//                throw new PluginException(e, pluginId);
//            } else {
//                throw new IllegalStateException(e);
//            }
//        } catch (Exception e) {
//            PluginId pluginId = stub.getPluginId();
//            if (pluginId != null) {
//                throw new PluginException("cannot create class \"" + className + "\"", e, pluginId);
//            } else {
//                throw new IllegalStateException("cannot create class \"" + className + "\"", e);
//            }
//        }
//
//        if (!(obj instanceof AnAction)) {
//            throw new IllegalStateException("class with name \"" + className + "\" should be instance of " + AnAction.class.getName());
//        }
//
//        AnAction anAction = (AnAction)obj;
//        stub.initAction(anAction);
//        if (StringUtil.isNotEmpty(stub.getText())) {
//            anAction.getTemplatePresentation().setText(stub.getText());
//        }
//        String iconPath = stub.getIconPath();
//        if (iconPath != null) {
//            setIconFromClass(anAction.getClass(), anAction.getClass().getClassLoader(), iconPath, stub.getClassName(),
//                             anAction.getTemplatePresentation(), stub.getPluginId());
//        }
//
//        myId2Action.put(stub.getId(), obj);
//        myAction2Id.put(obj, stub.getId());
//
//        return anAction;
//    }

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

//    public JComponent createButtonToolbar(final String actionPlace, final ActionGroup messageActionGroup) {
//        return new ButtonToolbarImpl(actionPlace, messageActionGroup, myDataManager, this);
//    }

    public Action getActionOrStub(String id) {
        return getActionImpl(id, true);
    }

//    /**
//     * @return instance of ActionGroup or ActionStub. The method never returns real subclasses
//     *         of <code>AnAction</code>.
//     */
//
//    private Action processActionElement(Element element, final ClassLoader loader, PluginId pluginId) {
//        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
//        ResourceBundle bundle = getActionsResourceBundle(loader, plugin);
//
//        if (!ACTION_ELEMENT_NAME.equals(element.getName())) {
//            reportActionError(pluginId, "unexpected name of element \"" + element.getName() + "\"");
//            return null;
//        }
//        String className = element.getAttributeValue(CLASS_ATTR_NAME);
//        if (className == null || className.length() == 0) {
//            reportActionError(pluginId, "action element should have specified \"class\" attribute");
//            return null;
//        }
//        // read ID and register loaded action
//        String id = element.getAttributeValue(ID_ATTR_NAME);
//        if (id == null || id.length() == 0) {
//            id = StringUtil.getShortName(className);
//        }
//        if (Boolean.valueOf(element.getAttributeValue(INTERNAL_ATTR_NAME)).booleanValue() &&
//            !ApplicationManagerEx.getApplicationEx().isInternal()) {
//            myNotRegisteredInternalActionIds.add(id);
//            return null;
//        }
//
//        String text = loadTextForElement(element, bundle, id, ACTION_ELEMENT_NAME);
//
//        String iconPath = element.getAttributeValue(ICON_ATTR_NAME);
//
//        if (text == null) {
//            String message = "'text' attribute is mandatory (action ID=" + id + ";" +
//                             (plugin == null ? "" : " plugin path: " + plugin.getPath()) + ")";
//            reportActionError(pluginId, message);
//            return null;
//        }
//
//        ActionStub stub = new ActionStub(className, id, text, loader, pluginId, iconPath);
//        Presentation presentation = stub.getTemplatePresentation();
//        presentation.setText(text);
//
//        // description
//
//        presentation.setDescription(loadDescriptionForElement(element, bundle, id, ACTION_ELEMENT_NAME));
//
//        // process all links and key bindings if any
//        for (final Object o : element.getChildren()) {
//            Element e = (Element)o;
//            if (ADD_TO_GROUP_ELEMENT_NAME.equals(e.getName())) {
//                processAddToGroupNode(stub, e, pluginId, isSecondary(e));
//            } else if (SHORTCUT_ELEMENT_NAME.equals(e.getName())) {
//                processKeyboardShortcutNode(e, id, pluginId);
//            } else if (MOUSE_SHORTCUT_ELEMENT_NAME.equals(e.getName())) {
//                processMouseShortcutNode(e, id, pluginId);
//            } else {
//                reportActionError(pluginId, "unexpected name of element \"" + e.getName() + "\"");
//                return null;
//            }
//        }
//        if (element.getAttributeValue(USE_SHORTCUT_OF_ATTR_NAME) != null) {
//            ((KeymapManagerEx)myKeymapManager).bindShortcuts(element.getAttributeValue(USE_SHORTCUT_OF_ATTR_NAME), id);
//        }
//
//        // register action
//        registerAction(id, stub, pluginId);
//        return stub;
//    }

//    private AnAction processGroupElement(Element element, final ClassLoader loader, PluginId pluginId) {
//        final IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
//        ResourceBundle bundle = getActionsResourceBundle(loader, plugin);
//
//        if (!GROUP_ELEMENT_NAME.equals(element.getName())) {
//            reportActionError(pluginId, "unexpected name of element \"" + element.getName() + "\"");
//            return null;
//        }
//        String className = element.getAttributeValue(CLASS_ATTR_NAME);
//        if (className == null) { // use default group if class isn't specified
//            className = DefaultActionGroup.class.getName();
//        }
//        try {
//            Class aClass = Class.forName(className, true, loader);
//            Object obj = new ConstructorInjectionComponentAdapter(className, aClass)
//                    .getComponentInstance(ApplicationManager.getApplication().getPicoContainer());
//
//            if (!(obj instanceof ActionGroup)) {
//                reportActionError(pluginId, "class with name \"" + className + "\" should be instance of " + ActionGroup.class.getName());
//                return null;
//            }
//            if (element.getChildren().size() != element.getChildren(ADD_TO_GROUP_ELEMENT_NAME).size()) {  //
//                if (!(obj instanceof DefaultActionGroup)) {
//                    reportActionError(pluginId,
//                                      "class with name \"" + className + "\" should be instance of " + DefaultActionGroup.class.getName
// () +
//                                      " because there are children specified");
//                    return null;
//                }
//            }
//            ActionGroup group = (ActionGroup)obj;
//            // read ID and register loaded group
//            String id = element.getAttributeValue(ID_ATTR_NAME);
//            if (id != null && id.length() == 0) {
//                reportActionError(pluginId, "ID of the group cannot be an empty string");
//                return null;
//            }
//            if (Boolean.valueOf(element.getAttributeValue(INTERNAL_ATTR_NAME)).booleanValue() &&
//                !ApplicationManagerEx.getApplicationEx().isInternal()) {
//                myNotRegisteredInternalActionIds.add(id);
//                return null;
//            }
//
//            if (id != null) {
//                registerAction(id, group);
//            }
//            Presentation presentation = group.getTemplatePresentation();
//
//            // text
//            String text = loadTextForElement(element, bundle, id, GROUP_ELEMENT_NAME);
//            // don't override value which was set in API with empty value from xml descriptor
//            if (!StringUtil.isEmpty(text) || presentation.getText() == null) {
//                presentation.setText(text);
//            }
//
//            // description
//            String description = loadDescriptionForElement(element, bundle, id, GROUP_ELEMENT_NAME);
//            // don't override value which was set in API with empty value from xml descriptor
//            if (!StringUtil.isEmpty(description) || presentation.getDescription() == null) {
//                presentation.setDescription(description);
//            }
//
//            // icon
//            setIcon(element.getAttributeValue(ICON_ATTR_NAME), className, loader, presentation, pluginId);
//            // popup
//            String popup = element.getAttributeValue(POPUP_ATTR_NAME);
//            if (popup != null) {
//                group.setPopup(Boolean.valueOf(popup).booleanValue());
//            }
//            // process all group's children. There are other groups, actions, references and links
//            for (final Object o : element.getChildren()) {
//                Element child = (Element)o;
//                String name = child.getName();
//                if (ACTION_ELEMENT_NAME.equals(name)) {
//                    AnAction action = processActionElement(child, loader, pluginId);
//                    if (action != null) {
//                        assertActionIsGroupOrStub(action);
//                        ((DefaultActionGroup)group).addAction(action, Constraints.LAST, this).setAsSecondary(isSecondary(child));
//                    }
//                } else if (SEPARATOR_ELEMENT_NAME.equals(name)) {
//                    processSeparatorNode((DefaultActionGroup)group, child, pluginId);
//                } else if (GROUP_ELEMENT_NAME.equals(name)) {
//                    AnAction action = processGroupElement(child, loader, pluginId);
//                    if (action != null) {
//                        ((DefaultActionGroup)group).add(action, this);
//                    }
//                } else if (ADD_TO_GROUP_ELEMENT_NAME.equals(name)) {
//                    processAddToGroupNode(group, child, pluginId, isSecondary(child));
//                } else if (REFERENCE_ELEMENT_NAME.equals(name)) {
//                    AnAction action = processReferenceElement(child, pluginId);
//                    if (action != null) {
//                        ((DefaultActionGroup)group).addAction(action, Constraints.LAST, this).setAsSecondary(isSecondary(child));
//                    }
//                } else {
//                    reportActionError(pluginId, "unexpected name of element \"" + name + "\n");
//                    return null;
//                }
//            }
//            return group;
//        } catch (ClassNotFoundException e) {
//            reportActionError(pluginId, "class with name \"" + className + "\" not found");
//            return null;
//        } catch (NoClassDefFoundError e) {
//            reportActionError(pluginId, "class with name \"" + e.getMessage() + "\" not found");
//            return null;
//        } catch (UnsupportedClassVersionError e) {
//            reportActionError(pluginId, "unsupported class version for " + className);
//            return null;
//        } catch (Exception e) {
//            final String message = "cannot create class \"" + className + "\"";
//            if (pluginId == null) {
//                LOG.error(message, e);
//            } else {
//                LOG.error(new PluginException(message, e, pluginId));
//            }
//            return null;
//        }
//    }
//
//    private void processReferenceNode(final Element element, final PluginId pluginId) {
//        final AnAction action = processReferenceElement(element, pluginId);
//
//        for (final Object o : element.getChildren()) {
//            Element child = (Element)o;
//            if (ADD_TO_GROUP_ELEMENT_NAME.equals(child.getName())) {
//                processAddToGroupNode(action, child, pluginId, isSecondary(child));
//            }
//        }
//    }

//    /**
//     * \
//     *
//     * @param element
//     *         description of link
//     * @param pluginId
//     * @param secondary
//     */
//    private void processAddToGroupNode(AnAction action, Element element, final PluginId pluginId, boolean secondary) {
//        // Real subclasses of AnAction should not be here
//        if (!(action instanceof Separator)) {
//            assertActionIsGroupOrStub(action);
//        }
//
//        String actionName = action instanceof ActionStub ? ((ActionStub)action).getClassName() : action.getClass().getName();
//
//        if (!ADD_TO_GROUP_ELEMENT_NAME.equals(element.getName())) {
//            reportActionError(pluginId, "unexpected name of element \"" + element.getName() + "\"");
//            return;
//        }
//
//        // parent group
//        final AnAction parentGroup = getParentGroup(element.getAttributeValue(GROUPID_ATTR_NAME), actionName, pluginId);
//        if (parentGroup == null) {
//            return;
//        }
//
//        // anchor attribute
//        final Anchor anchor = parseAnchor(element.getAttributeValue(ANCHOR_ELEMENT_NAME),
//                                          actionName, pluginId);
//        if (anchor == null) {
//            return;
//        }
//
//        final String relativeToActionId = element.getAttributeValue(RELATIVE_TO_ACTION_ATTR_NAME);
//        if (!checkRelativeToAction(relativeToActionId, anchor, actionName, pluginId)) {
//            return;
//        }
//        final DefaultActionGroup group = (DefaultActionGroup)parentGroup;
//        group.addAction(action, new Constraints(anchor, relativeToActionId), this).setAsSecondary(secondary);
//    }

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

//    /**
//     * @param parentGroup
//     *         group which is the parent of the separator. It can be <code>null</code> in that
//     *         case separator will be added to group described in the <add-to-group ....> subelement.
//     * @param element
//     *         XML element which represent separator.
//     */
//    private void processSeparatorNode( DefaultActionGroup parentGroup, Element element, PluginId pluginId) {
//        if (!SEPARATOR_ELEMENT_NAME.equals(element.getName())) {
//            reportActionError(pluginId, "unexpected name of element \"" + element.getName() + "\"");
//            return;
//        }
//        Separator separator = Separator.getInstance();
//        if (parentGroup != null) {
//            parentGroup.add(separator, this);
//        }
//        // try to find inner <add-to-parent...> tag
//        for (final Object o : element.getChildren()) {
//            Element child = (Element)o;
//            if (ADD_TO_GROUP_ELEMENT_NAME.equals(child.getName())) {
//                processAddToGroupNode(separator, child, pluginId, isSecondary(child));
//            }
//        }
//    }

//    private void processKeyboardShortcutNode(Element element, String actionId, PluginId pluginId) {
//        String firstStrokeString = element.getAttributeValue(FIRST_KEYSTROKE_ATTR_NAME);
//        if (firstStrokeString == null) {
//            reportActionError(pluginId, "\"first-keystroke\" attribute must be specified for action with id=" + actionId);
//            return;
//        }
//        KeyStroke firstKeyStroke = getKeyStroke(firstStrokeString);
//        if (firstKeyStroke == null) {
//            reportActionError(pluginId, "\"first-keystroke\" attribute has invalid value for action with id=" + actionId);
//            return;
//        }
//
//        KeyStroke secondKeyStroke = null;
//        String secondStrokeString = element.getAttributeValue(SECOND_KEYSTROKE_ATTR_NAME);
//        if (secondStrokeString != null) {
//            secondKeyStroke = getKeyStroke(secondStrokeString);
//            if (secondKeyStroke == null) {
//                reportActionError(pluginId, "\"second-keystroke\" attribute has invalid value for action with id=" + actionId);
//                return;
//            }
//        }
//
//        String keymapName = element.getAttributeValue(KEYMAP_ATTR_NAME);
//        if (keymapName == null || keymapName.trim().length() == 0) {
//            reportActionError(pluginId, "attribute \"keymap\" should be defined");
//            return;
//        }
//        Keymap keymap = myKeymapManager.getKeymap(keymapName);
//        if (keymap == null) {
//            reportActionError(pluginId, "keymap \"" + keymapName + "\" not found");
//            return;
//        }
//        final String removeOption = element.getAttributeValue(REMOVE_SHORTCUT_ATTR_NAME);
//        final KeyboardShortcut shortcut = new KeyboardShortcut(firstKeyStroke, secondKeyStroke);
//        final String replaceOption = element.getAttributeValue(REPLACE_SHORTCUT_ATTR_NAME);
//        if (Boolean.valueOf(removeOption)) {
//            keymap.removeShortcut(actionId, shortcut);
//        }
//        if (Boolean.valueOf(replaceOption)) {
//            keymap.removeAllActionShortcuts(actionId);
//        }
//        if (!Boolean.valueOf(removeOption)) {
//            keymap.addShortcut(actionId, shortcut);
//        }
//    }
//
//
//    private AnAction processReferenceElement(Element element, PluginId pluginId) {
//        if (!REFERENCE_ELEMENT_NAME.equals(element.getName())) {
//            reportActionError(pluginId, "unexpected name of element \"" + element.getName() + "\"");
//            return null;
//        }
//        String ref = element.getAttributeValue(REF_ATTR_NAME);
//
//        if (ref == null) {
//            // support old style references by id
//            ref = element.getAttributeValue(ID_ATTR_NAME);
//        }
//
//        if (ref == null || ref.length() == 0) {
//            reportActionError(pluginId, "ID of reference element should be defined");
//            return null;
//        }
//
//        AnAction action = getActionImpl(ref, true);
//
//        if (action == null) {
//            if (!myNotRegisteredInternalActionIds.contains(ref)) {
//                reportActionError(pluginId, "action specified by reference isn't registered (ID=" + ref + ")");
//            }
//            return null;
//        }
//        assertActionIsGroupOrStub(action);
//        return action;
//    }
//
//    private void processActionsChildElement(final ClassLoader loader, final PluginId pluginId, final Element child) {
//        String name = child.getName();
//        if (ACTION_ELEMENT_NAME.equals(name)) {
//            AnAction action = processActionElement(child, loader, pluginId);
//            if (action != null) {
//                assertActionIsGroupOrStub(action);
//            }
//        } else if (GROUP_ELEMENT_NAME.equals(name)) {
//            processGroupElement(child, loader, pluginId);
//        } else if (SEPARATOR_ELEMENT_NAME.equals(name)) {
//            processSeparatorNode(null, child, pluginId);
//        } else if (REFERENCE_ELEMENT_NAME.equals(name)) {
//            processReferenceNode(child, pluginId);
//        } else {
//            reportActionError(pluginId, "unexpected name of element \"" + name + "\n");
//        }
//    }

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

//    public void addActionPopup(final ActionPopupMenuImpl menu) {
//        myPopups.add(menu);
//    }
//
//    public void removeActionPopup(final ActionPopupMenuImpl menu) {
//        final boolean removed = myPopups.remove(menu);
//        if (removed && myPopups.isEmpty()) {
//            flushActionPerformed();
//        }
//    }

//    public void queueActionPerformedEvent(final Action action, ActionEvent event) {
//        if (!myPopups.isEmpty()) {
//            myQueuedNotifications.put(action, context);
//        } else {
//            fireAfterActionPerformed(action, context, event);
//        }
//    }

//    public boolean isActionPopupStackEmpty() {
//        return myPopups.isEmpty();
//    }

//    @Override
//    public boolean isTransparentOnlyActionsUpdateNow() {
//        return myTransparentOnlyUpdate;
//    }

//    private void flushActionPerformed() {
//        final Set<Action> actions = myQueuedNotifications.keySet();
//        for (final Action eachAction : actions) {
//            final DataContext eachContext = myQueuedNotifications.get(eachAction);
//            fireAfterActionPerformed(eachAction, eachContext, myQueuedNotificationsEvents.get(eachAction));
//        }
//        myQueuedNotifications.clear();
//        myQueuedNotificationsEvents.clear();
//    }

//    public void addAnActionListener(ActionListener listener) {
//        myActionListeners.add(listener);
//    }
//
//    public void addAnActionListener(final AnActionListener listener, final Disposable parentDisposable) {
//        addAnActionListener(listener);
//        Disposer.register(parentDisposable, new Disposable() {
//            public void dispose() {
//                removeAnActionListener(listener);
//            }
//        });
//    }

//    public void removeAnActionListener(AnActionListener listener) {
//        myActionListeners.remove(listener);
//    }

//    public void fireBeforeActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
//        if (action != null) {
//            myPrevPerformedActionId = myLastPreformedActionId;
//            myLastPreformedActionId = getId(action);
//            //noinspection AssignmentToStaticFieldFromInstanceMethod
//            IdeaLogger.ourLastActionId = myLastPreformedActionId;
//        }
//        for (AnActionListener listener : myActionListeners) {
//            listener.beforeActionPerformed(action, dataContext, event);
//        }
//    }
//
//    public void fireAfterActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
//        if (action != null) {
//            myPrevPerformedActionId = myLastPreformedActionId;
//            myLastPreformedActionId = getId(action);
//            //noinspection AssignmentToStaticFieldFromInstanceMethod
//            IdeaLogger.ourLastActionId = myLastPreformedActionId;
//        }
//        for (AnActionListener listener : myActionListeners) {
//            try {
//                listener.afterActionPerformed(action, dataContext, event);
//            } catch (AbstractMethodError ignored) {
//            }
//        }
//    }
//
//    @Override
//    public KeyboardShortcut getKeyboardShortcut( String actionId) {
//        AnAction action = ActionManager.getInstance().getAction(actionId);
//        final ShortcutSet shortcutSet = action.getShortcutSet();
//        final Shortcut[] shortcuts = shortcutSet.getShortcuts();
//        for (final Shortcut shortcut : shortcuts) {
//            // Shortcut can be MouseShortcut here.
//            // For example IdeaVIM often assigns them
//            if (shortcut instanceof KeyboardShortcut) {
//                final KeyboardShortcut kb = (KeyboardShortcut)shortcut;
//                if (kb.getSecondKeyStroke() == null) {
//                    return (KeyboardShortcut)shortcut;
//                }
//            }
//        }
//
//        return null;
//    }

//    public void fireBeforeEditorTyping(char c, DataContext dataContext) {
//        myLastTimeEditorWasTypedIn = System.currentTimeMillis();
//        for (AnActionListener listener : myActionListeners) {
//            listener.beforeEditorTyping(c, dataContext);
//        }
//    }

    public String getLastPreformedActionId() {
        return myLastPreformedActionId;
    }

    public String getPrevPreformedActionId() {
        return myPrevPerformedActionId;
    }

    public Set<String> getActionIds() {
        return new HashSet<String>(myId2Action.keySet());

    }

//    public void preloadActions() {
//        if (myPreloadActionsRunnable == null) {
//            myPreloadActionsRunnable = new Runnable() {
//                public void run() {
//                    try {
//                        doPreloadActions();
//                    } catch (RuntimeInterruptedException ignore) {
//                    }
//                }
//            };
//            ApplicationManager.getApplication().executeOnPooledThread(myPreloadActionsRunnable);
//        }
//    }
//
//    private void doPreloadActions() {
//        try {
//            Thread.sleep(5000); // wait for project initialization to complete
//        } catch (InterruptedException e) {
//            return; // IDEA exited
//        }
//        preloadActionGroup(IdeActions.GROUP_EDITOR_POPUP);
//        preloadActionGroup(IdeActions.GROUP_EDITOR_TAB_POPUP);
//        preloadActionGroup(IdeActions.GROUP_PROJECT_VIEW_POPUP);
//        preloadActionGroup(IdeActions.GROUP_MAIN_MENU);
//        // TODO anything else?
//        LOG.debug("Actions preloading completed");
//    }

//    public void preloadActionGroup(final String groupId) {
//        final AnAction action = getAction(groupId);
//        if (action instanceof ActionGroup) {
//            preloadActionGroup((ActionGroup)action);
//        }
//    }

//    private void preloadActionGroup(final ActionGroup group) {
//        final Application application = ApplicationManager.getApplication();
//        final AnAction[] children = application.runReadAction(new Computable<AnAction[]>() {
//            public AnAction[] compute() {
//                if (application.isDisposed()) {
//                    return AnAction.EMPTY_ARRAY;
//                }
//
//                return group.getChildren(null);
//            }
//        });
//        for (AnAction action : children) {
//            if (action instanceof PreloadableAction) {
//                ((PreloadableAction)action).preload();
//            } else if (action instanceof ActionGroup) {
//                preloadActionGroup((ActionGroup)action);
//            }
//
//            myActionsPreloaded++;
//            if (myActionsPreloaded % 10 == 0) {
//                try {
//                    //noinspection BusyWait
//                    Thread.sleep(300);
//                } catch (InterruptedException ignored) {
//                    throw new RuntimeInterruptedException(ignored);
//                }
//            }
//        }
//    }

//    public ActionCallback tryToExecute(final AnAction action, final java.awt.event.InputEvent inputEvent,
//                                       final java.awt.Component contextComponent, final String place,
//                                       boolean now) {
//
//        final Application app = ApplicationManager.getApplication();
//        assert app.isDispatchThread();
//
//        final ActionCallback result = new ActionCallback();
//        final Runnable doRunnable = new Runnable() {
//            public void run() {
//                tryToExecuteNow(action, inputEvent, contextComponent, place, result);
//            }
//        };
//
//        if (now) {
//            doRunnable.run();
//        } else {
//            //noinspection SSBasedInspection
//            SwingUtilities.invokeLater(doRunnable);
//        }
//
//        return result;
//
//    }
//
//    private void tryToExecuteNow(final AnAction action, final InputEvent inputEvent, final Component contextComponent, final String place,
//                                 final ActionCallback result) {
//        final Presentation presentation = action.getTemplatePresentation().clone();
//
//        IdeFocusManager.findInstanceByContext(getContextBy(contextComponent)).doWhenFocusSettlesDown(new Runnable() {
//            public void run() {
//                final DataContext context = getContextBy(contextComponent);
//
//                AnActionEvent event = new AnActionEvent(
//                        inputEvent, context,
//                        place != null ? place : ActionPlaces.UNKNOWN,
//                        presentation, ActionManagerImpl.this,
//                        inputEvent.getModifiersEx()
//                );
//
//                ActionUtil.performDumbAwareUpdate(action, event, false);
//                if (!event.getPresentation().isEnabled()) {
//                    result.setRejected();
//                    return;
//                }
//
//                ActionUtil.lastUpdateAndCheckDumb(action, event, false);
//                if (!event.getPresentation().isEnabled()) {
//                    result.setRejected();
//                    return;
//                }
//
//                Component component = PlatformDataKeys.CONTEXT_COMPONENT.getData(context);
//                if (component != null && !component.isShowing()) {
//                    result.setRejected();
//                    return;
//                }
//
//                fireBeforeActionPerformed(action, context, event);
//
//                UIUtil.addAwtListener(new AWTEventListener() {
//                    public void eventDispatched(AWTEvent event) {
//                        if (event.getID() == WindowEvent.WINDOW_OPENED || event.getID() == WindowEvent.WINDOW_ACTIVATED) {
//                            if (!result.isProcessed()) {
//                                final WindowEvent we = (WindowEvent)event;
//                                IdeFocusManager.findInstanceByComponent(we.getWindow())
//                                               .doWhenFocusSettlesDown(result.createSetDoneRunnable());
//                            }
//                        }
//                    }
//                }, AWTEvent.WINDOW_EVENT_MASK, result);
//
//                ActionUtil.performActionDumbAware(action, event);
//                result.setDone();
//                queueActionPerformedEvent(action, context, event);
//            }
//        });
//    }

//    private class MyTimer extends Timer implements ActionListener {
//        private final List<TimerListener> myTimerListeners            = Collections.synchronizedList(new ArrayList<TimerListener>());
//        private final List<TimerListener> myTransparentTimerListeners = Collections.synchronizedList(new ArrayList<TimerListener>());
//        private int myLastTimePerformed;
//
//        MyTimer() {
//            super(TIMER_DELAY, null);
//            addActionListener(this);
//            setRepeats(true);
//            final MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect();
//            connection.subscribe(ApplicationActivationListener.TOPIC, new ApplicationActivationListener() {
//                @Override
//                public void applicationActivated(IdeFrame ideFrame) {
//                    setDelay(TIMER_DELAY);
//                    restart();
//                }
//
//                @Override
//                public void applicationDeactivated(IdeFrame ideFrame) {
//                    setDelay(DEACTIVATED_TIMER_DELAY);
//                }
//            });
//        }
//
//        @Override
//        public String toString() {
//            return "Action manager timer";
//        }
//
//        public void addTimerListener(TimerListener listener, boolean transparent) {
//            if (transparent) {
//                myTransparentTimerListeners.add(listener);
//            } else {
//                myTimerListeners.add(listener);
//            }
//        }
//
//        public void removeTimerListener(TimerListener listener, boolean transparent) {
//            if (transparent) {
//                myTransparentTimerListeners.remove(listener);
//            } else {
//                myTimerListeners.remove(listener);
//            }
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            if (myLastTimeEditorWasTypedIn + UPDATE_DELAY_AFTER_TYPING > System.currentTimeMillis()) {
//                return;
//            }
//
//            if (IdeFocusManager.getInstance(null).isFocusBeingTransferred()) return;
//
//            final int lastEventCount = myLastTimePerformed;
//            myLastTimePerformed = ActivityTracker.getInstance().getCount();
//
//            boolean transparentOnly = myLastTimePerformed == lastEventCount;
//
//            try {
//                HashSet<TimerListener> notified = new HashSet<TimerListener>();
//                myTransparentOnlyUpdate = transparentOnly;
//                notifyListeners(myTransparentTimerListeners, notified);
//
//                if (transparentOnly) {
//                    return;
//                }
//
//                notifyListeners(myTimerListeners, notified);
//            } finally {
//                myTransparentOnlyUpdate = false;
//            }
//        }
//
//        private void notifyListeners(final List<TimerListener> timerListeners, final Set<TimerListener> notified) {
//            final TimerListener[] listeners = timerListeners.toArray(new TimerListener[timerListeners.size()]);
//            for (TimerListener listener : listeners) {
//                if (timerListeners.contains(listener)) {
//                    if (!notified.contains(listener)) {
//                        notified.add(listener);
//                        runListenerAction(listener);
//                    }
//                }
//            }
//        }
//
//        private void runListenerAction(final TimerListener listener) {
//            ModalityState modalityState = listener.getModalityState();
//            if (modalityState == null) return;
//            if (!ModalityState.current().dominates(modalityState)) {
//                try {
//                    listener.run();
//                } catch (ProcessCanceledException ex) {
//                    // ignore
//                } catch (Throwable e) {
//                    LOG.error(e);
//                }
//            }
//        }
//    }

}
