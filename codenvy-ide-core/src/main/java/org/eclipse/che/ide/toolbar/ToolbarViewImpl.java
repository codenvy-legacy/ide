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
package org.eclipse.che.ide.toolbar;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionGroup;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.CustomComponentAction;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.action.Separator;
import org.eclipse.che.ide.api.keybinding.KeyBindingAgent;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;

import javax.annotation.Nonnull;


/**
 * The implementation of {@link ToolbarView}
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToolbarViewImpl extends Composite implements ToolbarView {

    public static final int DELAY_MILLIS = 1000;
    Toolbar toolbar;
    private String          place;
    private ActionGroup     leftActionGroup;
    private ActionGroup     rightActionGroup;
    private ActionManager   actionManager;
    private KeyBindingAgent keyBindingAgent;

    private Array<Action> newLeftVisibleActions;
    private Array<Action> leftVisibleActions;

    private Array<Action> newRightVisibleActions;
    private Array<Action> rightVisibleActions;


    private PresentationFactory presentationFactory;
    private boolean             addSeparatorFirst;
    private final Timer timer = new Timer() {
        @Override
        public void run() {
            updateActions();
            schedule(DELAY_MILLIS);
        }
    };

    /** Create view with given instance of resources. */
    @Inject
    public ToolbarViewImpl(ActionManager actionManager, KeyBindingAgent keyBindingAgent) {
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        toolbar = new Toolbar();
        initWidget(toolbar);

        newLeftVisibleActions = Collections.createArray();
        leftVisibleActions = Collections.createArray();

        newRightVisibleActions = Collections.createArray();
        rightVisibleActions = Collections.createArray();

        presentationFactory = new PresentationFactory();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // ok
        // there are no events for now
    }

    @Override
    public void setPlace(@Nonnull String place) {
        this.place = place;
    }

    @Override
    public void setLeftActionGroup(@Nonnull ActionGroup leftActionGroup) {
        this.leftActionGroup = leftActionGroup;
        updateActions();
        if (!timer.isRunning())
            timer.schedule(DELAY_MILLIS);
    }

    @Override
    public void setRightActionGroup(@Nonnull ActionGroup rightActionGroup) {
        this.rightActionGroup = rightActionGroup;
        updateActions();
        if (!timer.isRunning())
            timer.schedule(DELAY_MILLIS);
    }

    //TODO need improve code
    private void updateActions() {
        if (leftActionGroup != null) {
            newLeftVisibleActions.clear();
            Utils.expandActionGroup(leftActionGroup, newLeftVisibleActions, presentationFactory, place, actionManager, false);
            if (!Collections.equals(newLeftVisibleActions, leftVisibleActions)) {
                final Array<Action> temp = leftVisibleActions;
                leftVisibleActions = newLeftVisibleActions;
                newLeftVisibleActions = temp;
                toolbar.clearMainPanel();
                fillLeftToolbar(leftVisibleActions);
            }
        }
        if (rightActionGroup != null) {
            newRightVisibleActions.clear();
            Utils.expandActionGroup(rightActionGroup, newRightVisibleActions, presentationFactory, place, actionManager, false);
            if (!Collections.equals(newRightVisibleActions, rightVisibleActions)) {
                final Array<Action> temp = rightVisibleActions;
                rightVisibleActions = newRightVisibleActions;
                newRightVisibleActions = temp;
                toolbar.clearRightPanel();
                fillRightToolbar(rightVisibleActions);
            }
        }
    }

    //TODO need improve code : dublicate code
    private void fillLeftToolbar(Array<Action> leftActions) {
        if (addSeparatorFirst) {
            toolbar.addToMainPanel(new DelimiterItem());
            toolbar.addToRightPanel(new DelimiterItem());
        }
        for (int i = 0; i < leftActions.size(); i++) {
            final Action action = leftActions.get(i);
            if (action instanceof Separator) {
                if (i > 0 && i < leftActions.size() - 1) {
                    toolbar.addToMainPanel(new DelimiterItem());
                }
            } else if (action instanceof CustomComponentAction) {
                Presentation presentation = presentationFactory.getPresentation(action);
                Widget customComponent = ((CustomComponentAction)action).createCustomComponent(presentation);
                toolbar.addToMainPanel(customComponent);
            } else if (action instanceof ActionGroup && !(action instanceof CustomComponentAction) && ((ActionGroup)action).isPopup()) {
                ActionPopupButton button =
                        new ActionPopupButton((ActionGroup)action, actionManager, keyBindingAgent, presentationFactory, place);
                toolbar.addToMainPanel(button);
            } else {
                final ActionButton button = createToolbarButton(action);
                toolbar.addToMainPanel(button);
            }
        }
    }

    //TODO need improve code : dublicate code
    private void fillRightToolbar(Array<Action> rightActions) {
        for (int i = 0; i < rightActions.size(); i++) {
            final Action action = rightActions.get(i);
            if (action instanceof Separator) {
                if (i > 0 && i < rightActions.size() - 1) {
                    toolbar.addToRightPanel(new DelimiterItem());
                }
            } else if (action instanceof CustomComponentAction) {
                Presentation presentation = presentationFactory.getPresentation(action);
                Widget customComponent = ((CustomComponentAction)action).createCustomComponent(presentation);
                toolbar.addToRightPanel(customComponent);
            } else if (action instanceof ActionGroup && !(action instanceof CustomComponentAction) && ((ActionGroup)action).isPopup()) {
                ActionPopupButton button =
                        new ActionPopupButton((ActionGroup)action, actionManager, keyBindingAgent, presentationFactory, place);
                toolbar.addToRightPanel(button);
            } else {
                final ActionButton button = createToolbarButton(action);
                toolbar.addToRightPanel(button);
            }
        }
    }


    private ActionButton createToolbarButton(Action action) {
        return new ActionButton(action, actionManager, presentationFactory.getPresentation(action), place);
    }


    @Override
    public void setAddSeparatorFirst(boolean addSeparatorFirst) {
        this.addSeparatorFirst = addSeparatorFirst;
    }
}