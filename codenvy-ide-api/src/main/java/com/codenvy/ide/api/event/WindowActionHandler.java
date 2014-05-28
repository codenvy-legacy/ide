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
package com.codenvy.ide.api.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles {@link WindowActionEvent}.
 *
 * @author Artem Zatsarynnyy
 */
public interface WindowActionHandler extends EventHandler {
    /**
     * Fired just before the Codenvy browser's tab closes or navigates to a different site.
     *
     * @param event
     *         {@link WindowActionEvent}
     */
    void onWindowClosing(WindowActionEvent event);

    /**
     * Fired after the Codenvy browser's tab closed or navigated to a different site.
     *
     * @param event
     *         {@link WindowActionEvent}
     */
    void onWindowClosed(WindowActionEvent event);
}
