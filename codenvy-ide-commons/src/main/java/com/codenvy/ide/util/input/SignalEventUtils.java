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

package com.codenvy.ide.util.input;

import elemental.events.Event;

/** Utility methods for dealing with {@link SignalEvent}. */
public class SignalEventUtils {

    public static SignalEvent create(Event rawEvent) {
        return SignalEventImpl.create((com.google.gwt.user.client.Event)rawEvent, true);
    }

    public static SignalEvent create(Event rawEvent, boolean cancelBubbleIfNullified) {
        return SignalEventImpl.create(
                (com.google.gwt.user.client.Event)rawEvent, cancelBubbleIfNullified);
    }

    /**
     * Returns the paste contents from a "paste" event, or null if it is not a
     * paste event or cannot be retrieved.
     */
    public static native String getPasteContents(Event event) /*-{
        if (!event.clipboardData || !event.clipboardData.getData) {
            return null;
        }

        return event.clipboardData.getData('text/plain');
    }-*/;
}
