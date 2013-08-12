/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.api.ui.action;

import com.codenvy.ide.util.Pair;
import com.codenvy.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A default implementation of {@link ActionGroup}. Provides the ability
 * to add children actions and separators between them. In most of the
 * cases you will be using this implementation but note that there are
 * cases where children are determined
 * on rules different than just positional constraints, that's when you need
 * to implement your own <code>ActionGroup</code>.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DefaultActionGroup extends ActionGroup {
    /** Contains instances of AnAction */
    private final List<Action>                    mySortedChildren = new ArrayList<Action>();
    /** Contains instances of Pair */
    private final List<Pair<Action, Constraints>> myPairs          = new ArrayList<Pair<Action, Constraints>>();
    private ActionManager actionManager;

    public DefaultActionGroup(ActionManager actionManager) {
        this(null, false, actionManager);
    }

    /**
     * Creates an action group containing the specified actions.
     *
     * @param actions
     *         the actions to add to the group
     */
    public DefaultActionGroup(ActionManager actionManager, Action... actions) {
        this(null, false, actionManager);
        for (Action action : actions) {
            add(action);
        }
    }

    public DefaultActionGroup(String shortName, boolean popup, ActionManager actionManager) {
        super(shortName, popup);
        this.actionManager = actionManager;
    }

    private static int findIndex(String actionId, List<Action> actions, ActionManager actionManager) {
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);

            String id = actionManager.getId(action);
            if (id != null && id.equals(actionId)) {
                return i;
            }

        }
        return -1;
    }

    /**
     * Adds the specified action to the tail.
     *
     * @param action
     *         Action to be added
     * @param actionManager
     *         ActionManager instance
     */
    public final void add(Action action, ActionManager actionManager) {
        add(action, new Constraints(Anchor.LAST, null), actionManager);
    }

    public final void add(Action action) {
        addAction(action, new Constraints(Anchor.LAST, null));
    }

    public final ActionInGroup addAction(Action action) {
        return addAction(action, new Constraints(Anchor.LAST, null));
    }

    /** Adds a separator to the tail. */
    public final void addSeparator() {
        add(Separator.getInstance());
    }

    public final void add(Action action, Constraints constraint) {
        add(action, constraint, actionManager);
    }

    public final ActionInGroup addAction(Action action, Constraints constraint) {
        return addAction(action, constraint, actionManager);
    }

    /**
     * Adds the specified action with the specified constraint.
     *
     * @param action
     *         Action to be added; cannot be null
     * @param constraint
     *         Constraint to be used for determining action's position; cannot be null
     * @throws IllegalArgumentException
     *         in case when:
     *         <li>action is null
     *         <li>constraint is null
     *         <li>action is already in the group
     */
    public final void add(Action action, Constraints constraint, ActionManager actionManager) {
        addAction(action, constraint, actionManager);
    }

    public final ActionInGroup addAction(Action action, Constraints constraint, ActionManager actionManager) {
        if (action == this) {
            throw new IllegalArgumentException("Cannot add a group to itself");
        }
        // Check that action isn't already registered
        if (!(action instanceof Separator)) {
            if (mySortedChildren.contains(action)) {
                throw new IllegalArgumentException("cannot add an action twice: " + action);
            }
            for (Pair<Action, Constraints> pair : myPairs) {
                if (action.equals(pair.first)) {
                    throw new IllegalArgumentException("cannot add an action twice: " + action);
                }
            }
        }

        constraint = constraint.clone();

        if (constraint.myAnchor == Anchor.FIRST) {
            mySortedChildren.add(0, action);
        } else if (constraint.myAnchor == Anchor.LAST) {
            mySortedChildren.add(action);
        } else {
            if (addToSortedList(action, constraint, actionManager)) {
                actionAdded(action, actionManager);
            } else {
                myPairs.add(new Pair<Action, Constraints>(action, constraint));
            }
        }

        return new ActionInGroup(this, action);
    }

    private void actionAdded(Action addedAction, ActionManager actionManager) {
        String addedActionId = actionManager.getId(addedAction);
        if (addedActionId == null) {
            return;
        }
        outer:
        while (!myPairs.isEmpty()) {
            for (int i = 0; i < myPairs.size(); i++) {
                Pair<Action, Constraints> pair = myPairs.get(i);
                if (addToSortedList(pair.first, pair.second, actionManager)) {
                    myPairs.remove(i);
                    continue outer;
                }
            }
            break;
        }
    }

    private boolean addToSortedList(Action action, Constraints constraint, ActionManager actionManager) {
        int index = findIndex(constraint.myRelativeToActionId, mySortedChildren, actionManager);
        if (index == -1) {
            return false;
        }
        if (constraint.myAnchor == Anchor.BEFORE) {
            mySortedChildren.add(index, action);
        } else {
            mySortedChildren.add(index + 1, action);
        }
        return true;
    }

    /**
     * Removes specified action from group.
     *
     * @param action
     *         Action to be removed
     */
    public final void remove(Action action) {
        if (!mySortedChildren.remove(action)) {
            for (int i = 0; i < myPairs.size(); i++) {
                Pair<Action, Constraints> pair = myPairs.get(i);
                if (pair.first.equals(action)) {
                    myPairs.remove(i);
                    break;
                }
            }
        }
    }

    /** Removes all children actions (separators as well) from the group. */
    public final void removeAll() {
        mySortedChildren.clear();
        myPairs.clear();
    }

    /**
     * Returns group's children in the order determined by constraints.
     *
     * @param e
     *         not used
     * @return An array of children actions
     */
    @Override
    public final Action[] getChildren(ActionEvent e) {
        boolean hasNulls = false;

        // Mix sorted actions and pairs
        int sortedSize = mySortedChildren.size();
        Action[] children = new Action[sortedSize + myPairs.size()];
        for (int i = 0; i < sortedSize; i++) {
            Action action = mySortedChildren.get(i);
            if (action == null) {
                Log.error(getClass(), "Empty sorted child: " + this + ", " + getClass() + "; index=" + i);
            }

            hasNulls |= action == null;
            children[i] = action;
        }
        for (int i = 0; i < myPairs.size(); i++) {
            final Pair<Action, Constraints> pair = myPairs.get(i);
            Action action = pair.first;
            if (action == null) {
                Log.error(getClass(), "Empty pair child: " + this + ", " + getClass() + "; index=" + i);
            }
            hasNulls |= action == null;
            children[i + sortedSize] = action;
        }

        if (hasNulls) {
            return mapNotNull(children, new Action[0]);
        }
        return children;
    }

    private Action[] mapNotNull(Action[] arr, Action[] emptyArray) {
        List<Action> result = new ArrayList<Action>(arr.length);
        for (Action t : arr) {
            if (t != null) {
                result.add(t);
            }
        }
        return result.toArray(emptyArray);
    }

    /**
     * Returns the number of contained children (including separators).
     *
     * @return number of children in the group
     */
    public final int getChildrenCount() {
        return mySortedChildren.size() + myPairs.size();
    }

    public final Action[] getChildActionsOrStubs() {
        // Mix sorted actions and pairs
        int sortedSize = mySortedChildren.size();
        Action[] children = new Action[sortedSize + myPairs.size()];
        for (int i = 0; i < sortedSize; i++) {
            children[i] = mySortedChildren.get(i);
        }
        for (int i = 0; i < myPairs.size(); i++) {
            children[i + sortedSize] = myPairs.get(i).first;
        }
        return children;
    }

    public final void addAll(ActionGroup group) {
        for (Action each : group.getChildren(null)) {
            add(each);
        }
    }

    public final void addAll(Collection<Action> actionList) {
        for (Action each : actionList) {
            add(each);
        }
    }

    public final void addAll(Action... actions) {
        for (Action each : actions) {
            add(each);
        }
    }

    public void addSeparator(String separatorText) {
        add(new Separator(separatorText));
    }
}
