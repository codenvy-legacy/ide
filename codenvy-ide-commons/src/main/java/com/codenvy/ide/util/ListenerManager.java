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

package com.codenvy.ide.util;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

/**
 * Lightweight manager for listeners that's designed to reduce boilerplate in
 * classes that have listeners.
 * <p/>
 * The client stores a final member for this class, and exposes a {@code
 * getListenerRegistrar()} with return type {@link ListenerRegistrar}. To
 * dispatch, the client calls {@link #dispatch(Dispatcher)} with a custom
 * dispatcher. (If the dispatches are frequent, consider keeping a single
 * dispatcher instance whose state you set prior to passing it to the dispatch
 * method.)
 *
 * @param <L>
 *         the type of the listener
 */
public class ListenerManager<L> implements ListenerRegistrar<L> {

    /**
     * Dispatches to a listener.
     *
     * @param <L>
     *         the type of the listener
     */
    public interface Dispatcher<L> {
        void dispatch(L listener);
    }

    /**
     * Listener that is notified when clients are added or removed from this
     * manager.
     */
    public interface RegistrationListener<L> {
        void onListenerAdded(L listener);

        void onListenerRemoved(L listener);
    }

    public static <L> ListenerManager<L> create() {
        return new ListenerManager<L>(null);
    }

    public static <L> ListenerManager<L> create(RegistrationListener<L> registrationListener) {
        return new ListenerManager<L>(registrationListener);
    }

    private boolean isDispatching;

    private final JsonArray<L> listeners;

    /** Listeners that were added during a dispatch */
    private final JsonArray<L> queuedListenerAdditions;

    /** Listeners that were removed during a dispatch */
    private final JsonArray<L> queuedListenerRemovals;

    private final RegistrationListener<L> registrationListener;

    private ListenerManager(RegistrationListener<L> registrationListener) {
        this.listeners = JsonCollections.createArray();
        this.queuedListenerAdditions = JsonCollections.createArray();
        this.queuedListenerRemovals = JsonCollections.createArray();
        this.registrationListener = registrationListener;
    }

    /** Adds a new listener to this event. */
    @Override
    public Remover add(final L listener) {
        if (!isDispatching) {
            addListenerImpl(listener);
        } else {
            if (!queuedListenerRemovals.remove(listener)) {
                queuedListenerAdditions.add(listener);
            }
        }

        return new Remover() {
            @Override
            public void remove() {
                ListenerManager.this.remove(listener);
            }
        };
    }

    /** Dispatches this event to all listeners. */
    public void dispatch(final Dispatcher<L> dispatcher) {
        isDispatching = true;
        try {
            for (int i = 0, n = listeners.size(); i < n; i++) {
                dispatcher.dispatch(listeners.get(i));
            }
        } finally {
            isDispatching = false;
            addQueuedListeners();
            removeQueuedListeners();
        }
    }

    /**
     * Removes a listener from this manager.
     * <p/>
     * It is strongly preferred that you use the {@link ListenerRegistrar.Remover}
     * returned by {@link #add(Object)} instead of calling this method directly.
     */
    @Override
    public void remove(L listener) {
        if (!isDispatching) {
            removeListenerImpl(listener);
        } else {
            if (!queuedListenerAdditions.remove(listener)) {
                queuedListenerRemovals.add(listener);
            }
        }
    }

    /**
     * Returns the number of listeners registered on this manager. This does not
     * include those listeners that are queued to be added and it does include
     * those listeners that are queued to be removed.
     */
    public int getCount() {
        return listeners.size();
    }

    /** Returns true if the listener manager is currently dispatching to listeners. */
    public boolean isDispatching() {
        return isDispatching;
    }

    private void addQueuedListeners() {
        for (int i = 0, n = queuedListenerAdditions.size(); i < n; i++) {
            addListenerImpl(queuedListenerAdditions.get(i));
        }
        queuedListenerAdditions.clear();
    }

    private void removeQueuedListeners() {
        for (int i = 0, n = queuedListenerRemovals.size(); i < n; i++) {
            removeListenerImpl(queuedListenerRemovals.get(i));
        }
        queuedListenerRemovals.clear();
    }

    private void addListenerImpl(final L listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);

            if (registrationListener != null) {
                registrationListener.onListenerAdded(listener);
            }
        }
    }

    private void removeListenerImpl(final L listener) {
        if (listeners.remove(listener) && registrationListener != null) {
            registrationListener.onListenerRemoved(listener);
        }
    }
}
