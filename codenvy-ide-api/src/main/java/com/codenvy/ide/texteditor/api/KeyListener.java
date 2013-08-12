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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.util.input.SignalEvent;

/** A listener that is called when the user presses a key. */
public interface KeyListener {
   /*
    * The reason for preventDefault() not preventing default behavior is that
    * Firefox does not have support the defaultPrevented attribute, so we have
    * know way of knowing if it was prevented from the native event. We could
    * create a proxy for SignalEvent to note calls to preventDefault(), but
    * this would not catch the case that the implementor interacts directly to
    * the native event.
    */

    /**
     * @param event
     *         the event for the key press. Note: Calling preventDefault()
     *         may not prevent the default behavior in some cases. The return
     *         value of this method is a better channel for indicating the
     *         default behavior should be prevented.
     * @return true if the event was handled (the default behavior will not run
     *         in this case), false to proceed with the default behavior. Even
     *         if true is returned, other listeners will still get the callback
     */
    boolean onKeyPress(SignalEvent event);
}