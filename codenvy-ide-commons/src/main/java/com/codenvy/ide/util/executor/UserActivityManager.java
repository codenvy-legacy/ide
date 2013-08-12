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

package com.codenvy.ide.util.executor;

import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;
import com.google.gwt.user.client.Timer;

/**
 * A class that manages the active status of the user so that other objects can
 * be intelligent about performing computationally intensive work.
 */
public class UserActivityManager {

    private static final int IDLE_DELAY_MS = 400;

    /**
     * A listener that is called when the user either becomes idle or becomes
     * active.
     */
    public interface UserActivityListener {
        /** Called when the user is considered idle. */
        void onUserIdle();

        /**
         * Called when the user is considered active. This may be called
         * synchronously from critical paths (scrolling), so avoid intensive work.
         */
        void onUserActive();
    }

    private final Dispatcher<UserActivityListener> activeListenerDispatcher =
            new Dispatcher<UserActivityManager.UserActivityListener>() {
                @Override
                public void dispatch(UserActivityListener listener) {
                    listener.onUserActive();
                }
            };

    private final Dispatcher<UserActivityListener> idleListenerDispatcher =
            new Dispatcher<UserActivityManager.UserActivityListener>() {
                @Override
                public void dispatch(UserActivityListener listener) {
                    listener.onUserIdle();
                }
            };

    private boolean isUserActive = false;

    private final ListenerManager<UserActivityListener> userActivityListenerManager = ListenerManager.create();

    private final Timer switchToIdleTimer = new Timer() {
        @Override
        public void run() {
            handleUserIdle();
        }
    };

    public ListenerRegistrar<UserActivityListener> getUserActivityListenerRegistrar() {
        return userActivityListenerManager;
    }

    public boolean isUserActive() {
        return isUserActive;
    }

    public void markUserActive() {
        switchToIdleTimer.schedule(IDLE_DELAY_MS);

        if (isUserActive) {
            return;
        }

        isUserActive = true;
        userActivityListenerManager.dispatch(activeListenerDispatcher);
    }

    private void handleUserIdle() {
        if (!isUserActive) {
            return;
        }

        isUserActive = false;
        userActivityListenerManager.dispatch(idleListenerDispatcher);
    }
}
