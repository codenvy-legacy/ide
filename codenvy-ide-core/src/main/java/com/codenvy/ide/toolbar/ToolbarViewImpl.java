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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.Separator;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;


/**
 * The implementation of {@link ToolbarView}
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToolbarViewImpl extends Composite implements ToolbarView {

    public static final int DELAY_MILLIS = 1000;
    Toolbar toolbar;
    private       String              place;
    private       ActionGroup         actionGroup;
    private       ActionManager       actionManager;
    private       KeyBindingAgent     keyBindingAgent;
    private       Array<Action>       newVisibleActions;
    private       Array<Action>       visibleActions;
    private       PresentationFactory presentationFactory;
    private       boolean             addSeparatorFirst;
    private final DefaultActionGroup  secondaryActions;
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
        newVisibleActions = Collections.createArray();
        visibleActions = Collections.createArray();
        presentationFactory = new PresentationFactory();
        secondaryActions = new DefaultActionGroup(actionManager);
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
    public void setActionGroup(@Nonnull ActionGroup actionGroup) {
        this.actionGroup = actionGroup;
        updateActions();
        timer.schedule(DELAY_MILLIS);
    }

    private void updateActions() {
        newVisibleActions.clear();
        Utils.expandActionGroup(actionGroup, newVisibleActions, presentationFactory, place, actionManager, false);
        if (!Collections.equals(newVisibleActions, visibleActions)) {
            final Array<Action> temp = visibleActions;
            visibleActions = newVisibleActions;
            newVisibleActions = temp;
            removeAll();
            secondaryActions.removeAll();
            fillToolbar(visibleActions, true);
        }
    }

    private void fillToolbar(Array<Action> actions, boolean layoutSecondaries) {
        if (addSeparatorFirst) {
            toolbar.add(new DelimiterItem());
        }
        for (int i = 0; i < actions.size(); i++) {
            final Action action = actions.get(i);
//            if (action instanceof Separator && isNavBar()) {
//                continue;
//            }

            if (layoutSecondaries) {
                if (!actionGroup.isPrimary(action)) {
                    secondaryActions.add(action);
                    continue;
                }
            }

            if (action instanceof Separator) {
                if (i > 0 && i < actions.size() - 1) {
                    toolbar.add(new DelimiterItem());
                }
            } else if (action instanceof CustomComponentAction) {
                Presentation presentation = presentationFactory.getPresentation(action);
                Widget customComponent = ((CustomComponentAction)action).createCustomComponent(presentation);
//                presentation.putClientProperty(CustomComponentAction.CUSTOM_COMPONENT_PROPERTY, customComponent);
                toolbar.add(customComponent);
            } else if (action instanceof ActionGroup && !(action instanceof CustomComponentAction) && ((ActionGroup)action).isPopup()) {
                ActionPopupButton button =
                        new ActionPopupButton((ActionGroup)action, actionManager, keyBindingAgent, presentationFactory, place);
                toolbar.add(button);
            } else {
                final ActionButton button = createToolbarButton(action);
                toolbar.add(button);
            }
        }

//        if (secondaryActions.getChildrenCount() > 0) {
//            secondaryActionsButton = new ActionButton(secondaryActions, presentationFactory.getPresentation(secondaryActions), place);
//            secondaryActionsButton.setNoIconsInPopup(true);
//            add(mySecondaryActionsButton);
//        }
    }

    private ActionButton createToolbarButton(Action action) {
        return new ActionButton(action, actionManager, presentationFactory.getPresentation(action), place);
    }

    private void removeAll() {
        toolbar.clear();
    }

    @Override
    public void setAddSeparatorFirst(boolean addSeparatorFirst) {
        this.addSeparatorFirst = addSeparatorFirst;
    }
}