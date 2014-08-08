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
package com.codenvy.ide.api.texteditor;

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