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
package com.codenvy.ide.api.ui.action;


import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.codenvy.ide.util.input.KeyMapUtil;
import com.google.gwt.resources.client.ImageResource;

/**
 * Represents an entity that has a state, a presentation and can be performed.
 * <p/>
 * For an action to be useful, you need to implement {@link Action#actionPerformed}
 * and optionally to override {@link Action#update}. By overriding the
 * {@link Action#update} method you can dynamically change action's presentation
 * depending on the place (for more information on places see {@link ActionPlaces}.
 * <p/>
 * The same action can have various presentations.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class Action {
    private Presentation          myTemplatePresentation;
    private CharCodeWithModifiers myShortcut;
    private boolean               myEnabledInModalContext;

    /** Creates a new action with its text, description and icon set to <code>null</code>. */
    public Action() {
        this(null, null, null);
    }

    /**
     * Creates a new action with <code>icon</code> provided. Its text, description set to <code>null</code>.
     *
     * @param icon
     *         Default icon to appear in toolbars and menus (Note some platform don't have icons in menu).
     */
    public Action(ImageResource icon) {
        this(null, null, icon);
    }

    /**
     * Creates a new action with the specified text. Description and icon are
     * set to <code>null</code>.
     *
     * @param text
     *         Serves as a tooltip when the presentation is a button and the name of the
     *         menu item when the presentation is a menu item.
     */
    public Action(String text) {
        this(text, null, null);
    }

    /**
     * Constructs a new action with the specified text, description and icon.
     *
     * @param text
     *         Serves as a tooltip when the presentation is a button and the name of the
     *         menu item when the presentation is a menu item
     * @param description
     *         Describes current action, this description will appear on
     *         the status bar when presentation has focus
     * @param icon
     *         Action's icon
     */
    public Action(String text, String description, ImageResource icon) {
        myEnabledInModalContext = false;
        Presentation presentation = getTemplatePresentation();
        presentation.setText(text);
        presentation.setDescription(description);
        presentation.setIcon(icon);
    }

    /**
     * Returns the shortcut set associated with this action.
     *
     * @return shortcut set associated with this action
     */
    public final CharCodeWithModifiers getShortcut() {
        return myShortcut;
    }

    /**
     * Copies template presentation and shortcuts set from <code>sourceAction</code>.
     *
     * @param sourceAction
     *         cannot be <code>null</code>
     */
    public final void copyFrom(Action sourceAction) {
        Presentation sourcePresentation = sourceAction.getTemplatePresentation();
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(sourcePresentation.getIcon());
        presentation.setText(sourcePresentation.getTextWithMnemonic());
        presentation.setDescription(sourcePresentation.getDescription());
        copyShortcutFrom(sourceAction);
    }

    public final void copyShortcutFrom(Action sourceAction) {
        myShortcut = sourceAction.myShortcut;
    }


    public final boolean isEnabledInModalContext() {
        return myEnabledInModalContext;
    }

    protected final void setEnabledInModalContext(boolean enabledInModalContext) {
        myEnabledInModalContext = enabledInModalContext;
    }

    /** Override with true returned if your action has to display its text along with the icon when placed in the toolbar */
    public boolean displayTextInToolbar() {
        return false;
    }

    /**
     * Updates the state of the action. Default implementation does nothing.
     * Override this method to provide the ability to dynamically change action's
     * state and(or) presentation depending on the context (For example
     * when your action state depends on the selection you can check for
     * selection and change the state accordingly).
     * This method can be called frequently, for instance, if an action is added to a toolbar,
     * it will be updated twice a second. This means that this method is supposed to work really fast,
     * no real work should be done at this phase. For example, checking selection in a tree or a list,
     * is considered valid, but working with a file system is not. If you cannot understand the state of
     * the action fast you should do it in the {@link #actionPerformed(ActionEvent)} method and notify
     * the user that action cannot be executed if it's the case.
     *
     * @param e
     *         Carries information on the invocation place and data available
     */
    public void update(ActionEvent e) {
    }

    /**
     * Same as {@link #update(ActionEvent)} but is calls immediately before actionPerformed() as final check guard.
     * Default implementation delegates to {@link #update(ActionEvent)}.
     *
     * @param e
     *         Carries information on the invocation place and data available
     */
    public void beforeActionPerformedUpdate(ActionEvent e) {
        update(e);
        if (!e.getPresentation().isEnabled()) {
            e.setInjectedContext(false);
            update(e);
        }
    }

    /**
     * Returns a template presentation that will be used
     * as a template for created presentations.
     *
     * @return template presentation
     */
    public final Presentation getTemplatePresentation() {
        Presentation presentation = myTemplatePresentation;
        if (presentation == null) {
            myTemplatePresentation = presentation = new Presentation();
        }
        return presentation;
    }

    /**
     * Implement this method to provide your action handler.
     *
     * @param e
     *         Carries information on the invocation place
     */
    public abstract void actionPerformed(ActionEvent e);

    protected void setShortcut(CharCodeWithModifiers shortcutSet) {
        myShortcut = shortcutSet;
    }

    public static String createTooltipText(String s, Action action) {
        String toolTipText = s == null ? "" : s;
        while (StringUtils.endsWithChar(toolTipText, '.')) {
            toolTipText = toolTipText.substring(0, toolTipText.length() - 1);
        }
        String shortcutsText = KeyMapUtil.getShortcutText(action.getShortcut());
        if (!shortcutsText.isEmpty()) {
            toolTipText += " (" + shortcutsText + ")";
        }
        return toolTipText;
    }


    public boolean isTransparentUpdate() {
        return this instanceof TransparentUpdate;
    }


    public interface TransparentUpdate {
    }

    @Override
    public String toString() {
        return getTemplatePresentation().toString();
    }
}
