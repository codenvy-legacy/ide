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


/** A manager to register or unregister listeners. */
public interface ListenerRegistrar<T> {

    /** A handle to allow removing the added listener. */
    public interface Remover {
        void remove();
    }

    /**
     * An object which helps to simplify management of multiple handlers that need
     * to be removed. This is the recommended approach to managing removers as it
     * guards against null checks and prevents forgetting to remove listeners.
     */
    public static class RemoverManager implements Remover {
        private JsonArray<Remover> handlers;

        /** Tracks a new handler so that it can be removed in bulk. */
        public RemoverManager track(Remover remover) {
            if (handlers == null) {
                handlers = JsonCollections.createArray();
            }

            handlers.add(remover);
            return this;
        }

        /** Removes all tracked handlers and clears the stored list of handlers. */
        @Override
        public void remove() {
            if (handlers == null) {
                return;
            }

            for (int i = 0; i < handlers.size(); i++) {
                handlers.get(i).remove();
            }

            handlers.clear();
        }
    }

    /** Registers a new listener. */
    Remover add(T listener);

    /**
     * Removes a listener. It is strongly preferred you use the {@link Remover}
     * returned by {@link #add(Object)} instead of calling this method directly.
     */
    void remove(T listener);
}
