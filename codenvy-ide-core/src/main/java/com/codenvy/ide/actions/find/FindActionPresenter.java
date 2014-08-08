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
package com.codenvy.ide.actions.find;

import com.codenvy.ide.actions.ActionManagerImpl;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.IdeActions;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.UnicodeUtils;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class FindActionPresenter implements Presenter, FindActionView.ActionDelegate {

    private final PresentationFactory presentationFactory;
    private       FindActionView      view;
    private       ActionManager       actionManager;
    private final Map<Action, String> actionsMap;
    private final Comparator<Action>  actionComparator = new Comparator<Action>() {
        @Override
        public int compare(Action o1, Action o2) {
            int compare = compare(o1.getTemplatePresentation().getText(), o2.getTemplatePresentation().getText());
            if (compare == 0 && !o1.equals(o2)) {
                return o1.hashCode() - o2.hashCode();
            }
            return compare;
        }

        public int compare(@Nullable String o1, @Nullable String o2) {
            if (o1 == null) return o2 == null ? 0 : -1;
            if (o2 == null) return 1;
            return o1.compareTo(o2);
        }
    };

    @Inject
    public FindActionPresenter(FindActionView view, ActionManager actionManager) {
        this.view = view;
        this.actionManager = actionManager;
        view.setDelegate(this);
        presentationFactory = new PresentationFactory();
        actionsMap = new TreeMap<>(actionComparator);
    }

    @Override
    public void go(AcceptsOneWidget container) {
    }

    public void show() {
        view.show();
        Action action = actionManager.getAction(IdeActions.GROUP_MAIN_MENU);
        collectActions(actionsMap, (ActionGroup)action, action.getTemplatePresentation().getText());
        view.focusOnInput();
        if (view.getName() != null) {
            nameChanged(view.getName(), view.getCheckBoxState());
        }

    }

    private void collectActions(Map<Action, String> result, ActionGroup group, final String containingGroupName) {
        final Action[] actions = group.getChildren(null);
        includeGroup(result, group, actions, containingGroupName);
        for (Action action : actions) {
            if (action != null) {
                if (action instanceof ActionGroup) {
                    final ActionGroup actionGroup = (ActionGroup)action;
                    final String groupName = actionGroup.getTemplatePresentation().getText();
                    collectActions(result, actionGroup,
                                   StringUtils.isNullOrEmpty(groupName) || !actionGroup.isPopup() ? containingGroupName : groupName);
                } else {
                    final String groupName = group.getTemplatePresentation().getText();
                    if (result.containsKey(action)) {
                        result.put(action, null);
                    }
                    else {
                        result.put(action, StringUtils.isNullOrEmpty(groupName) ? containingGroupName : groupName);
                    }
                }
            }
        }
    }

    private void includeGroup(Map<Action, String> result,
                              ActionGroup group,
                              Action[] actions,
                              String containingGroupName) {
        boolean showGroup = true;
        for (Action action : actions) {
            if (actionManager.getId(action) != null) {
                showGroup = false;
                break;
            }
        }
        if (showGroup) {
            result.put(group, containingGroupName);
        }
    }

    @Override
    public void nameChanged(String name, boolean checkBoxState) {
        if(name.isEmpty()){
            view.hideActions();
            return;
        }
        String pattern = convertPattern(name.trim());
        RegExp regExp = RegExp.compile(pattern);
        Map<Action,String> actions = new TreeMap<>(actionComparator);
        if (checkBoxState) {
            Set<String> ids = ((ActionManagerImpl)actionManager).getActionIds();
            for (Action action : actionsMap.keySet()) {
                ids.remove(actionManager.getId(action));
            }
            for (String id : ids) {
                Action action = actionManager.getAction(id);
                Presentation presentation = action.getTemplatePresentation();
                String text = presentation.getText();
                if (regExp.test(text)) {
                    actions.put(action, null);
                }
            }

        }
        for (Action action : actionsMap.keySet()) {
            Presentation presentation = action.getTemplatePresentation();
            String text = presentation.getText();
            String description = presentation.getDescription();
            if (text != null && regExp.test(text) ||
                description != null && !description.equals(text) && regExp.test(description)) {
                actions.put(action, actionsMap.get(action));
            } else {
                String groupName = actionsMap.get(action);
                if(groupName != null && text != null && regExp.test(groupName + " " + text)){
                    actions.put(action, groupName);
                }
            }

        }
        if(!actions.isEmpty()) {
            view.showActions(actions);
        }else{
            view.hideActions();
        }
    }

    @Override
    public void onClose() {
        actionsMap.clear();
    }

    @Override
    public void onActionSelected(Action action) {
        ActionEvent e = new ActionEvent("", presentationFactory.getPresentation(action), actionManager, 0);
        action.update(e);
        if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
            view.hide();
            action.actionPerformed(e);
        }
    }

    private String convertPattern(String pattern) {
        final int eol = pattern.indexOf('\n');
        if (eol != -1) {
            pattern = pattern.substring(0, eol);
        }
        if (pattern.length() >= 80) {
            pattern = pattern.substring(0, 80);
        }

        final StringBuilder buffer = new StringBuilder();

        boolean allowToLower = true;
        if (containsOnlyUppercaseLetters(pattern)) {
            allowToLower = false;
        }

        if (allowToLower) {
            buffer.append(".*");
        }

        boolean firstIdentifierLetter = true;
        for (int i = 0; i < pattern.length(); i++) {
            final char c = pattern.charAt(i);
            if (Character.isLetterOrDigit(c) || UnicodeUtils.regexpIdentifierOrWhitespace.test(String.valueOf(c))) {
                // This logic allows to use uppercase letters only to catch the name like PDM for PsiDocumentManager
                if (Character.isUpperCase(c) || Character.isDigit(c)) {

                    if (!firstIdentifierLetter) {
                        buffer.append("[^A-Z]*");
                    }

                    buffer.append("[");
                    buffer.append(c);
                    if (allowToLower || i == 0) {
                        buffer.append('|');
                        buffer.append(Character.toLowerCase(c));
                    }
                    buffer.append("]");
                }
                else if (Character.isLowerCase(c)) {
                    buffer.append('[');
                    buffer.append(c);
                    buffer.append('|');
                    buffer.append(Character.toUpperCase(c));
                    buffer.append(']');
                }
                else {
                    buffer.append(c);
                }

                firstIdentifierLetter = false;
            }
            else if (c == '*') {
                buffer.append(".*");
                firstIdentifierLetter = true;
            }
            else if (c == '.') {
                buffer.append("\\.");
                firstIdentifierLetter = true;
            }
            else if (c == ' ') {
                buffer.append("[^A-Z]*\\ ");
                firstIdentifierLetter = true;
            }
            else {
                firstIdentifierLetter = true;
                // for standard RegExp engine
                buffer.append("\\u");
                buffer.append(Integer.toHexString(c + 0x20000).substring(1));

                // for OROMATCHER RegExp engine
//                buffer.append("\\x");
//                buffer.append(Integer.toHexString(c + 0x20000).substring(3));
            }
        }

        buffer.append("*");
        return buffer.toString();
    }

    private static boolean containsOnlyUppercaseLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '*' && c != ' ' && !Character.isUpperCase(c)) return false;
        }
        return true;
    }
}
