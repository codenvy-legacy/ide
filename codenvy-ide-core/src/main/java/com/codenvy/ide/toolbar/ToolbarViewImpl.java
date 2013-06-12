/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.CustomComponentAction;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.api.ui.action.Separator;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * The implementation of {@link ToolbarView}
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ToolbarViewImpl extends Composite implements ToolbarView {

    public static final int DELAY_MILLIS = 1000;
    Toolbar toolbar;
    private       String              place;
    private       ActionGroup         actionGroup;
    private       ActionManager       actionManager;
    private       JsonArray<Action>   newVisibleActions;
    private       JsonArray<Action>   visibleActions;
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
    public ToolbarViewImpl(ActionManager actionManager) {
        this.actionManager = actionManager;
        toolbar = new Toolbar();
        initWidget(toolbar);
        newVisibleActions = JsonCollections.createArray();
        visibleActions = JsonCollections.createArray();
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
    public void setPlace(@NotNull String place) {
        this.place = place;
    }

    @Override
    public void setActionGroup(@NotNull ActionGroup actionGroup) {
        this.actionGroup = actionGroup;
        updateActions();
        timer.schedule(DELAY_MILLIS);
    }

    private void updateActions() {
        newVisibleActions.clear();
        Utils.expandActionGroup(actionGroup, newVisibleActions, presentationFactory, place, actionManager, false);
        if (!JsonCollections.equals(newVisibleActions, visibleActions)) {
            final JsonArray<Action> temp = visibleActions;
            visibleActions = newVisibleActions;
            newVisibleActions = temp;
            removeAll();
            secondaryActions.removeAll();
            fillToolbar(visibleActions, true);
        }
    }

    private void fillToolbar(JsonArray<Action> actions, boolean layoutSecondaries) {
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
                ActionPopupButton button = new ActionPopupButton((ActionGroup)action, actionManager, presentationFactory, place);
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