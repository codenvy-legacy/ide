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
package com.codenvy.ide.api.ui.action;


import com.codenvy.ide.util.ListenerManager;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.HashSet;
import java.util.Set;

/** @author Evgen Vidolob */
public abstract class ActionGroup extends Action {
    private boolean myPopup;
    private final       ListenerManager<PropertyChangeListener> myChangeSupport = ListenerManager.create();
    public static final ActionGroup                             EMPTY_GROUP     = new ActionGroup() {
        @Override
        public Action[] getChildren(ActionEvent e) {
            return new Action[0];
        }
    };

    private Set<Action> mySecondaryActions;

    /** The actual value is a Boolean. */
    public static final String PROP_POPUP = "popup";

    private Boolean myDumbAware;

    /**
     * Creates a new <code>ActionGroup</code> with shortName set to <code>null</code> and
     * popup set to false.
     */
    public ActionGroup() {
        this(null, false);
    }

    /**
     * Creates a new <code>ActionGroup</code> with the specified shortName
     * and popup.
     *
     * @param shortName
     *         Text that represents a short name for this action group
     * @param popup
     *         <code>true</code> if this group is a popup, <code>false</code>
     *         otherwise
     */
    public ActionGroup(String shortName, boolean popup) {
        super(shortName);
        setPopup(popup);
    }

    public ActionGroup(String text, String description, ImageResource icon, SVGResource svgIcon) {
        super(text, description, icon, svgIcon);
    }

    /** This method can be called in popup menus if {@link #canBePerformed()} is true */
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void update(ActionEvent e) {
        super.update(e);
    }

    /** @return true if {@link #actionPerformed(ActionEvent)} should be called */
    public boolean canBePerformed() {
        return false;
    }

    /**
     * Returns the type of the group.
     *
     * @return <code>true</code> if the group is a popup, <code>false</code> otherwise
     */
    public boolean isPopup() {
        return myPopup;
    }

    /**
     * Sets the type of the group.
     *
     * @param popup
     *         If <code>true</code> the group will be shown as a popup in menus
     */
    public final void setPopup(boolean popup) {
        boolean oldPopup = myPopup;
        myPopup = popup;
        firePropertyChange(PROP_POPUP, oldPopup ? Boolean.TRUE : Boolean.FALSE, myPopup ? Boolean.TRUE : Boolean.FALSE);
    }

    public final void addPropertyChangeListener(PropertyChangeListener l) {
        myChangeSupport.add(l);
    }

    public final void removePropertyChangeListener(PropertyChangeListener l) {
        myChangeSupport.remove(l);
    }

    protected final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        final PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        myChangeSupport.dispatch(new ListenerManager.Dispatcher<PropertyChangeListener>() {
            @Override
            public void dispatch(PropertyChangeListener listener) {
                listener.onPropertyChange(event);
            }
        });
    }

    /**
     * Returns the children of the group.
     *
     * @return An array representing children of this group. All returned children must be not <code>null</code>.
     */
    public abstract Action[] getChildren(ActionEvent e);

    final void setAsPrimary(Action action, boolean isPrimary) {
        if (isPrimary) {
            if (mySecondaryActions != null) {
                mySecondaryActions.remove(action);
            }
        } else {
            if (mySecondaryActions == null) {
                mySecondaryActions = new HashSet<>();
            }

            mySecondaryActions.add(action);
        }
    }

    public final boolean isPrimary(Action action) {
        return mySecondaryActions == null || !mySecondaryActions.contains(action);
    }

    protected final void replace(Action originalAction, Action newAction) {
        if (mySecondaryActions != null) {
            if (mySecondaryActions.contains(originalAction)) {
                mySecondaryActions.remove(originalAction);
                mySecondaryActions.add(newAction);
            }
        }
    }

    public boolean hideIfNoVisibleChildren() {
        return false;
    }

    public boolean disableIfNoVisibleChildren() {
        return true;
    }
}
